package com.example.circleweb.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CircleController {

    public static class CircleRequest {
        public double x;
        public double y;
        public double radius;
        public String apiType;
    }

    interface DrawingAPI {
        String drawCircle(double x, double y, double radius);
    }

    class OpenGLAPI implements DrawingAPI {
        public String drawCircle(double x, double y, double radius) {
            String message = String.format("OpenGL: Desenhando círculo em (%.1f, %.1f) com raio %.1f", x, y, radius);
            System.out.println(message);
            return message;
        }
    }

    class DirectXAPI implements DrawingAPI {
        public String drawCircle(double x, double y, double radius) {
            String message = String.format("DirectX: Desenhando círculo em (%.1f, %.1f) com raio %.1f", x, y, radius);
            System.out.println(message);
            return message;
        }
    }

    abstract class Shape {
        protected DrawingAPI drawingAPI;

        protected Shape(DrawingAPI drawingAPI) {
            this.drawingAPI = drawingAPI;
        }

        public abstract String draw();
    }

    class Circle extends Shape {
        private double x, y, radius;

        public Circle(double x, double y, double radius, DrawingAPI drawingAPI) {
            super(drawingAPI);
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        @Override
        public String draw() {
            return drawingAPI.drawCircle(x, y, radius);
        }
    }

    @PostMapping("/draw")
    public Map<String, Object> drawCircle(@RequestBody CircleRequest request) {
        DrawingAPI api;
        switch (request.apiType.toLowerCase()) {
            case "opengl":
                api = new OpenGLAPI();
                break;
            case "directx":
                api = new DirectXAPI();
                break;
            default:
                throw new IllegalArgumentException("API inválida: " + request.apiType);
        }
    
        Shape circle = new Circle(request.x, request.y, request.radius, api);
        String drawMessage = circle.draw();
    
        return Map.of(
            "x", request.x,
            "y", request.y,
            "radius", request.radius,
            "api", request.apiType,
            "message", drawMessage,
            "success", true
        );
    }
}