package com.luka.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;

@RestController
public class controller {
    @Autowired
    private DiscoveryClient discoveryClient;

    private List<String> recievedMessages = new ArrayList<>();

    @PostMapping("/recieve")
    public String recieveMessage(String message) {
        recievedMessages.add(message);
        return "message recieved: " + message;
    }

    @GetMapping("/services")
    public String sendMessage() {
        List<String> services = discoveryClient.getServices();
        return "services: " + services;
    }

    @GetMapping("/services/{serviceName}")
    public String sendMessageWithName(@PathParam("serviceName") String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

        ServiceInstance serviceInstance = instances.get(0);
        return "instance host: " + serviceInstance.getHost() + " id:" + serviceInstance.getInstanceId() + " metadata: " +
                serviceInstance.getMetadata() + " uri:" + serviceInstance.getUri()+" scheme"+serviceInstance.getScheme()+" port"+serviceInstance.getPort();
    }
}
