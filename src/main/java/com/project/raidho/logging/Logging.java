package com.project.raidho.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Logging {

    public static String getPrintStackTrace(Exception e) {
        StringWriter error = new StringWriter(); // 문자열 기록
        e.printStackTrace(new PrintWriter(error)); // 형식화된 표현을 텍스트 출력 스트림에 출력한다.
        return error.toString();
    }

}
