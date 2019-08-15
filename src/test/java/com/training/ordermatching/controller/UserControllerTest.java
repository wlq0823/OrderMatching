package com.training.ordermatching.controller;

import com.training.ordermatching.model.User;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserController userController;

    @Before
    public void setup(){
        mvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void loginTest()throws Exception{
        MvcResult mr = mvc.perform(MockMvcRequestBuilders.get("/orderMatching/user/login")
                .accept(MediaType.APPLICATION_JSON)
                .param("user_name", "test_trader")
                .param("password", "123456"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
        System.out.println("print " + mr.getResponse().getContentAsString());
    }

    @Test
    public void registerTest()throws Exception{

        MvcResult mr = mvc.perform(MockMvcRequestBuilders.post("/orderMatching/user/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(JSON.toJSONString(null))
                .andReturn());
        System.out.println("print " + mr.getResponse().getContentAsString());
    }
}
