package com.algo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class RunnersTest {
    Runner runner = new MainRunner();
    InputStream sysInBackup = System.in; // backup System.in to restore it later

    @Test
    public void navigationTestExit() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream("0\n0\n0\n4\n".getBytes());
        System.setIn(in);
        Scanner sc = new Scanner(System.in);
        assertTrue(runner.run(sc));
    }

    @Test
    public void navigationTestBack() throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream("0\n0\n1\n3\n3".getBytes());
        System.setIn(in);
        Scanner sc = new Scanner(System.in);
        assertTrue(runner.run(sc));
    }

    @After
    public void after() {
        System.setIn(sysInBackup);
    }

}