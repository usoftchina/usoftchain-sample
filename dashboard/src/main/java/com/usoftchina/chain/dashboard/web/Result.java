package com.usoftchina.chain.dashboard.web;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author yingp
 * @date 2017/6/20
 */
public class Result {

    private static final String successParam = "success";

    private static final String responseCodeParam = "code";

    private static final String responseMessageParam = "message";

    private static final String responseContentParam = "content";

    public static ModelMap success() {
        return new ModelMap(successParam, true);
    }

    public static <T> ModelMap success(T content) {
        return success().addAttribute(responseContentParam, content);
    }

    public static ModelMap error() {
        return new ModelMap(successParam, false);
    }

    public static ModelMap error(String message) {
        return error().addAttribute(responseMessageParam, message);
    }

    public static <T> ModelMap error(int code) {
        return error().addAttribute(responseCodeParam, code);
    }

    public static <T> ModelMap error(int code, String message) {
        return error(code).addAttribute(responseMessageParam, message);
    }

    public static ResponseEntity ok() {
        return ResponseEntity.ok(success());
    }

    public static <T> ResponseEntity ok(T content) {
        return ResponseEntity.ok(success(content));
    }

    public static <T> void ok(HttpServletResponse response, T content) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8.toString());
        PrintWriter printWriter = response.getWriter();
        printWriter.append(JSON.toJSONString(success(content)));
        printWriter.flush();
        printWriter.close();
    }

    public static <T> void ok(HttpServletResponse response) throws IOException {
        ok(response, null);
    }

    public static ResponseEntity badRequest() {
        // do not use ResponseEntity.badRequest()
        return ResponseEntity.ok(error());
    }

    public static <T> ResponseEntity badRequest(String message) {
        return ResponseEntity.ok(error(message));
    }

    public static <T> void badRequest(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8.toString());
        PrintWriter printWriter = response.getWriter();
        printWriter.append(JSON.toJSONString(error(code, message)));
        printWriter.flush();
        printWriter.close();
    }

    public static <T> void badRequest(HttpServletResponse response, String message) throws IOException {
        badRequest(response, HttpStatus.BAD_REQUEST.value(), message);
    }

    public static <T> void badRequest(HttpServletResponse response) throws IOException {
        badRequest(response, HttpStatus.BAD_REQUEST.value(), null);
    }

}
