package net.guides.springboot2.springboot2webappjsp;

import net.guides.springboot2.springboot2webappjsp.controllers.FacialController;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

public class FunctionTest {

    @Test
    public void test() throws JSONException {



        JSONObject jsonObject = new JSONObject();
        jsonObject.put("faceid1", "test");
        jsonObject.put("faceid2", "hello");

        System.out.println(jsonObject.toString());


        FacialController facialController = new FacialController();


        facialController.faceVerify("522e87b9-9b83-4039-a05f-f8bd91c611cd");




    }



}
