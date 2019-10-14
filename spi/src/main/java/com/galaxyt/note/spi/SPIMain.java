package com.galaxyt.note.spi;

import com.sun.tools.javac.util.ServiceLoader;

public class SPIMain {

    public static void main(String[] args) {

        ServiceLoader<People> people = ServiceLoader.load(People.class);

        for (People p : people) {
            p.say();
        }

    }


}
