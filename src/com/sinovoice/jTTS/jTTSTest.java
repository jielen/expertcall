package com.sinovoice.jTTS;

import java.io.*;

import com.sinovoice.jTTS.jTTS_JavaFastSyn;

public class jTTSTest{
    
    public static void main(String [] args)
    {
        //创建一个jTTS对象
        jTTS_JavaFastSyn jTTS = new jTTS_JavaFastSyn();
        
        //设置log
        jTTS.jTTS_SetLog("./Test.log", "DEBUG", 1, 1);
        
        //设置参数
        int nRet = jTTS.jTTS_SetParam("FILE", "xiaokun", "PCM16K16B", "DEFAULT",
                                       "GB", "common", "SYNC",
                                       6, 6, 6, 
                                       "NULL", "NULL", "NULL",
                                       "NULL", "FLAT",
                                       0, 0, "REPEAT"
                                       );
    
        if (nRet == 0)
        {
            File f = new File("GB.txt");

            InputStream input = null;
            try
            {
                // 通过子类实例化
                input = new FileInputStream(f);
            }
            catch (Exception e) { 
            	e.printStackTrace();
            }

            byte byText[] = null;
            int len = (int)f.length();
            if (len <= 0)
            {
                System.out.println("Input File Error.");
                return;
            }

            try
            {
                byText = new byte[len];
                int nlen = input.read(byText);
                if (nlen != len)
                {
                    System.out.println("Read File Error.");
                    return;
                }
            }
            catch (Exception e) { }

            try
            {
                input.close();
            }
            catch (Exception e) { }

           //合成到文件
           nRet = jTTS.jTTS_PlayToFile(byText, "TEXT", "GB",
                                      "gb.wav", "PCM16K16B", "DEFAULT",
                                      "SYNC", "XiaoKun", "common",
                                      -1, -1, -1,
                                      "PUNC_OFF", "DIGIT_TELEGRAM", "ENG_LETTER",
                                      "TAG_JTTS", "FLAT",
                                      2, 70, "NULL"
                                      );
        }
        
        if(nRet != 0)
        {
            System.out.println("jTTS_FastSynth = " + nRet);
        }
        System.out.println("jTTS_FastSynth OK");
    }
}