package com.kevin.android.redorblack.utility;

import android.support.annotation.Keep;

import java.util.UUID;
@Keep
public class StringGenerator {
        public static void main(String[] args) {
            System.out.println(generateString());
        }

        public static String generateString() {
            String uuid = UUID.randomUUID().toString();
            return "uuid = " + uuid;
        }
    }

