package com.example.demo;


import com.altima.api.sugar.enums.ModuleEnum;
import com.altima.api.sugar.enums.RecordFieldsEnum;
import com.altima.api.sugar.service.ISugarSearchService;
import com.altima.api.sugar.service.query.SugarQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

import static com.altima.api.sugar.service.query.api.SugarFilterApi.and;
import static com.altima.api.sugar.service.query.api.SugarFilterApi.equal;
import static com.altima.api.sugar.service.query.api.SugarQueryApi.module;

@RestController
public class GreetingController {


    private ISugarSearchService sugarSearchService;

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    GreetingController(ISugarSearchService sugarSearchService) {
        this.sugarSearchService = sugarSearchService;
    }

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @RequestMapping("/sugar")
    public void sugar() {
        System.out.println("sugar");
        SugarQuery callQuery = module(ModuleEnum.CONTACT)
                .withFilter(and(
                        equal(RecordFieldsEnum.PARENT_TYPE.getCode(), ModuleEnum.CONTACT.getCode())));

        sugarSearchService.filter(callQuery)
                .forEach(sugarRecord -> System.out.println("callQuery" + sugarRecord));


    }
}