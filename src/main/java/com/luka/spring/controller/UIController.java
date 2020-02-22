package com.luka.spring.controller;

import com.luka.spring.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UIController {

    @Autowired
    private EurekaDiscoveryClient discoveryClient;

    @RequestMapping("")
    public String getIndex(Model model) {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("lukasub");
        String host = "No hosts found";
        List<String> services = getServices();
        if (serviceInstances.size() > 0)
            host = serviceInstances.get(0).getHost();
        model.addAttribute("host", host);
        model.addAttribute("services", services);
        model.addAttribute("message", new Message());
        String message = (String) model.getAttribute("messages");
        model.addAttribute("messages", message == null ? "" : message);
        return "index";
    }

    public List<String> getServices() {
        List<String> services = discoveryClient.getServices();
        return services.stream()
                .map(service -> discoveryClient.getInstances(service))
                .map(instance -> instance.isEmpty() ?
                        "No instances" : "appname: " + instance.get(0).getServiceId() +
                        " : host:" + instance.get(0).getHost())
                .collect(Collectors.toList());

    }

    @PostMapping("/send")
    public ModelAndView sendMessage(@ModelAttribute Message message, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("messages", message.getMessage());
        return new ModelAndView("redirect:/");
    }
}
