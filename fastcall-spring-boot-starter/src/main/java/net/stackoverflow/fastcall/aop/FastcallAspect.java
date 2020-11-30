package net.stackoverflow.fastcall.aop;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FastcallAspect {

    @Pointcut("@target(net.stackoverflow.fastcall.annotation.FastcallReference)")
    public void callPointCut() {

    }

    @Before("callPointCut()")
    public void before() {
        System.out.println("before..............");
    }

    @After("callPointCut()")
    public void after() {
        System.out.println("after...............");
    }
}
