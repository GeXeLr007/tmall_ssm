package com.how2java.tmall.test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Main{
    public static void main(String[] args) throws IOException {
        WritableResource resource1 = new PathResource("C:\\Users\\gxl17\\Documents\\workspaces\\IdeaProjects\\tmall_ssm\\src\\main\\resources\\file1.txt");
//        Resource resource2 = new ClassPathResource("file1.txt");

        OutputStream stream1 = resource1.getOutputStream();
        stream1.write("欢迎观临\n小春论坛".getBytes());
        stream1.close();

        InputStream ins1 = resource1.getInputStream();
//        InputStream res2 = resource2.getInputStream();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        while ((i=ins1.read()) != -1){
            byteArrayOutputStream.write(i);
        }

        System.out.println(byteArrayOutputStream.toString());

        System.out.println(resource1.getFilename());
//        System.out.println(resource2.getFilename());

    }
    
    
}