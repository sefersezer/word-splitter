package com.paritus;


import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;

import java.io.IOException;
import java.util.List;

/**
 * Created by Gokhan on 06.05.2014.
 */
public class Test {

    @org.junit.Test
    public void testSample() throws IOException {



        TurkishWordSplitter splitter = new TurkishWordSplitter();
        splitter.setMinimumWordLength(2);
        splitter.setStrictMode(false);

        StopWatch perf = new LoggingStopWatch("Splitter","Execution time of splitter");

        perf.start();
        List<String> test10 = splitter.splitWord("atatürkmahallesiekincioğlusokaknoAtaşehirİstanbul");
        perf.stop("Splitter",test10.toString());

        perf.start();
        List<String> test1 = splitter.splitWord("hakkındakesinleşenmahkemekararıbulunan");
        perf.stop("Splitter",test1.toString());

        perf.start();
        List<String> test2 = splitter.splitWord("Cumhurbaşkanıadayımerakkonusuoldu");
        perf.stop("Splitter",test2.toString());

        perf.start();
        List<String> test3 = splitter.splitWord("sosyalmedyahesaplarındaveözelgörüşmelerinde");
        perf.stop("Splitter",test3.toString());

        perf.start();
        List<String> test4 = splitter.splitWord("kulübümüzleilgisiolmayanenufakolayıbahaneederek");
        perf.stop("Splitter",test4.toString());


        perf.start();
        List<String> test5 = splitter.splitWord("sertaçıklamasınayanıtverdi");
        perf.stop("Splitter",test5.toString());


        perf.start();
        List<String> test6 = splitter.splitWord("bugrubadahilolmayanoyunlar");
        perf.stop("Splitter",test6.toString());

        perf.start();
        List<String> test7 = splitter.splitWord("ırksayısıaltıyadüşmüşdurumda");
        perf.stop("Splitter",test7.toString());

        perf.start();
        List<String> test8 = splitter.splitWord("oyunugeliştirilmeyecekanlamınagelmiyor");
        perf.stop("Splitter",test8.toString());

        perf.start();
        List<String> test9 = splitter.splitWord("üçüncüsınavaçokçalışacağınısöyleyenöğrenci ");
        perf.stop("Splitter",test9.toString());



    }
}
