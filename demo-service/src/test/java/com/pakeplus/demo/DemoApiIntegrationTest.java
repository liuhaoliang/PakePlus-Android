package com.pakeplus.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("演示接口集成测试")
class DemoApiIntegrationTest {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_NAME = "X-User-Name";
    private static final String HEADER_USER_TYPE = "X-User-Type";
    private static final String HEADER_USER_LEVEL = "X-User-Level";
    private static final String HEADER_USER_DEPT = "X-User-Dept";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Hello 接口返回问候语与服务器时间")
    void should_returnGreeting_when_callHello() throws Exception {
        mockMvc.perform(get("/api/demo/hello").param("name", "PakePlus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.greeting").value("Hello, PakePlus!"))
                .andExpect(jsonPath("$.data.server_time").exists());
    }

    @Test
    @DisplayName("哈希接口返回摘要，空文本返回参数错误")
    void should_returnDigest_when_callHash() throws Exception {
        mockMvc.perform(post("/api/demo/hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"abc\",\"algorithm\":\"SHA-256\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.digest")
                        .value("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"));

        mockMvc.perform(post("/api/demo/hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value("A0400"));
    }

    @Test
    @DisplayName("排序接口返回升序结果与统计")
    void should_returnSortedValues_when_callSort() throws Exception {
        mockMvc.perform(post("/api/demo/sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"values\":[5,1,4,2,8],\"order\":\"asc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sorted[0]").value(1))
                .andExpect(jsonPath("$.data.sorted[4]").value(8))
                .andExpect(jsonPath("$.data.comparisons").isNumber())
                .andExpect(jsonPath("$.data.swaps").isNumber());
    }

    @Test
    @DisplayName("三个接口的调用会被埋点并按维度聚合")
    void should_trackCallsAndAggregate_when_callApis() throws Exception {
        mockMvc.perform(get("/api/demo/hello").param("name", "Tracker")
                        .header(HEADER_USER_ID, "u9001")
                        .header(HEADER_USER_NAME, "埋点测试")
                        .header(HEADER_USER_TYPE, "INTERNAL")
                        .header(HEADER_USER_LEVEL, "P6")
                        .header(HEADER_USER_DEPT, "质量部"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/demo/sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"values\":[2,1],\"order\":\"asc\"}")
                        .header(HEADER_USER_ID, "u9001")
                        .header(HEADER_USER_TYPE, "INTERNAL")
                        .header(HEADER_USER_LEVEL, "P6")
                        .header(HEADER_USER_DEPT, "质量部"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/demo/track/summary").param("dimension", "user_dept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dimension_code").value("user_dept"))
                .andExpect(jsonPath("$.data.items[?(@.key=='质量部')].count").exists());
    }

    @Test
    @DisplayName("Hello 页面结果可导出为 CSV")
    void should_exportCsv_when_callExportHello() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/demo/export/hello").param("name", "导出"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        org.hamcrest.Matchers.containsString("hello-")))
                .andReturn();

        String csv = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertThat(csv).contains("问候语").contains("Hello, 导出!");
        assertThat(result.getResponse().getContentType()).contains("text/csv");
    }

    @Test
    @DisplayName("报表页面结果可导出为 CSV")
    void should_exportCsv_when_callExportTrack() throws Exception {
        String csv = mockMvc.perform(get("/api/demo/export/track").param("dimension", "user_type"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(csv).contains("统计维度").contains("总调用次数").contains("时间桶");
    }

    @Test
    @DisplayName("不支持的导出模块返回参数错误")
    void should_returnBadRequest_when_exportModuleUnknown() throws Exception {
        mockMvc.perform(get("/api/demo/export/unknown"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("演示数据生成接口返回生成条数")
    void should_generateSampleData_when_callSampleData() throws Exception {
        mockMvc.perform(post("/api/demo/track/sample-data").param("count", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.generated").value(12));
    }
}
