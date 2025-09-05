package com.personal.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest  // 加载完整应用上下文
@AutoConfigureMockMvc  // 自动配置MockMvc
public class DictControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void printDataSource() throws SQLException {
        // 打印测试环境连接的数据库URL
        log.info("测试数据库连接: {}", dataSource.getConnection().getMetaData().getURL());
    }

    @Test
    public void testDictPageWithTextBlock() throws Exception {
        // Java 17文本块语法：用"""包裹，直接保留JSON的换行和缩进
        String requestJson = """
            {
              "pageNo": 1,
              "pageSize": 10,
              "query": {

              },
              "sort": {
                "createdTime": "desc"
              }
            }
            """;


        // 打印请求参数（便于追踪测试用例）
        log.info("===== 请求参数 =====");
        log.info(requestJson);

        // 设置Mock行为
        //when(dictService.page(any())).thenReturn(null);

        // 执行请求并处理响应
        MvcResult mvcResult = mockMvc.perform(post("/api/dict/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                // 通过日志打印响应信息
                .andDo(result -> {
                    // 响应状态码
                    log.info("\n===== 响应状态码 =====");
                    log.info(String.valueOf(result.getResponse().getStatus()));

                    // 响应体
                    log.info("\n===== 响应体 =====");
                    log.info(result.getResponse().getContentAsString());
                })
                .andReturn();

        // 3. 可选：进一步处理响应（如解析后打印关键字段）
        String responseBody = mvcResult.getResponse().getContentAsString();
        log.info("\n===== 分页总条数 =====");
        // 从响应体中提取total字段（需结合JSON解析工具）
        // 示例：使用JsonPath提取（需引入com.jayway.jsonpath:json-path依赖）
        // Object total = JsonPath.read(responseBody, "$.data.total");
        // log.info(total.toString());
    }

    /**
     * 使用Java 17文本块（Text Blocks）定义JSON请求参数
     * 优势：保留原始JSON格式，无需转义换行符，修改直观
     */
    @Test
    public void testDictPage() throws Exception {
        // Java 17文本块语法：用"""包裹，直接保留JSON的换行和缩进
        String requestJson = """
            {
              "pageNo": 1,
              "pageSize": 10,
              "query": {
                "id": 123,
                "dictCode_like": "EAD",
                "createdTime_le": "2025-08-28 18:32:26",
                "createdTime_ge": "2025-08-28 18:32:26"
              },
              "sort": {
                "createTime": "desc"
              }
            }
            """;


        // 打印请求参数（便于追踪测试用例）
        log.info("===== 请求参数 =====");
        log.info(requestJson);

        // 设置Mock行为
        //when(dictService.page(any())).thenReturn(null);

        // 执行请求并处理响应
        MvcResult mvcResult = mockMvc.perform(post("/api/dict/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists())
                // 通过日志打印响应信息
                .andDo(result -> {
                    // 响应状态码
                    log.info("\n===== 响应状态码 =====");
                    log.info(String.valueOf(result.getResponse().getStatus()));

                    // 响应体
                    log.info("\n===== 响应体 =====");
                    log.info(result.getResponse().getContentAsString());
                })
                .andReturn();

        // 3. 可选：进一步处理响应（如解析后打印关键字段）
        String responseBody = mvcResult.getResponse().getContentAsString();
        log.info("\n===== 分页总条数 =====");
        // 从响应体中提取total字段（需结合JSON解析工具）
        // 示例：使用JsonPath提取（需引入com.jayway.jsonpath:json-path依赖）
        // Object total = JsonPath.read(responseBody, "$.data.total");
        // log.info(total.toString());
    }

    /**
     * 测试修改参数场景（基于文本块快速调整）
     */
    @Test
    public void testDictPageWithModifiedTextBlock() throws Exception {
        // 直接在文本块中修改参数，格式清晰
        String requestJson = """
            {
              "pageNo": 3,
              "pageSize": 15,
              "query": {
                "type_like": "EAD",
                "createTime_gte": "2025-01-01 00:00:00"  // 仅保留开始时间条件
              },
              "sort": {
                "type": "asc",  // 调整排序字段和方向
                "createTime": "desc"
              }
            }
            """;

        mockMvc.perform(post("/api/dict/page")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.pageNo").value(3))
                .andExpect(jsonPath("$.data.pageSize").value(15));
    }

}