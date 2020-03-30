package edu.udo.cs.rvs.ssdp;

import java.io.*;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Worker implements Runnable {

    byte[] byteArray;
    public InputStream inputStream ;
    public InputStreamReader streamReader ;
    public BufferedReader reader;
    public DatagramPacket dataReceived;
    public DatagramPacket dataFromList;
    public String zeile1;
    public String[] lines; //lines from datagram splitted by "\n"

    public static LinkedList<String> storeLinesForPrinting; //will be used to store the text for output when call by user

    public static boolean flag = true;

    public static void stopRunning()
    {
        flag = false;
    }


    /**
     * Linked list with the lines ready the be printed from the User class
     */
    Worker()
    {
        storeLinesForPrinting =  new LinkedList<>();
    }

    @Override


/**
 * First the function checks if the queue from which it gets the datagrams is empty
 * When there is an element "dataFromList", we transform it into a String, "dataLines",
 * then split this String so that we can have access to each line separately.
 * Then we iterate through each line, checking first if it's the kind of datagram we are
 * interested in (HTTP/1.1 or NOTIFY) and extract the information we need.
 * We also keep a variable "aliveOrbyebye" to check if we actually have to add it to the list
 *
 */
    public void run() {
        while(flag) {
            //System.out.println("running worker");
            while(Listen.queue.isEmpty()) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
            synchronized (Listen.queue){

                dataFromList = Listen.queue.removeFirst();
            }

            //does this have to be in the synchronized block?
            byteArray = dataFromList.getData();                             //getting the data from the datagram
            String dataLines = new String(byteArray,StandardCharsets.UTF_8);        //transforming the whole datagram into a String


            lines = dataLines.split("\\r?\\n");
            String stringToPrint="";
            if (lines[0].startsWith("HTTP/1.1") || lines[0].startsWith("NOTIFY"))

            {
                String aliveOrbyebye = "";   //to verify if we will keep the datagram
                String USN = "";
                String NTS = "";
                String ST = "";
                String NT = "";

                String[] zeile;

                for (int i = 1; i < lines.length; i++) //iterating each line of one datagram (starting from 1 because at 1 was http or notify)
                {
                    zeile = lines[i].split(":",2);  //splitting each line of a packet at ":"

                    switch (zeile[0]) {
                        case "USN": {

                            USN = zeile[1].split(":", 2)[1];    // getting what comes after USN:
                            USN = USN.split(":", 2)[0];     // getting just de part with random  numbers and letters
                            break;

                        }

                        case "ST": {

                            ST = zeile[1].replaceAll("\\s","");
                            break;

                        }

                        case "NT":{

                            NT = zeile[1].replaceAll("\\s","");
                            break;

                        }

                        case "NTS":{

                            aliveOrbyebye = zeile[1];
                            break;

                        }

                    }
                }
                if ( !ST.equals(""))                        //Unicast message
                    stringToPrint = USN + " - " + ST;
                else                                        //Multicast message
                    stringToPrint = USN + " - " + NT;

                synchronized (storeLinesForPrinting)
                {
                    if( aliveOrbyebye.contains("alive"))
                        storeLinesForPrinting.add(stringToPrint);
                }

            }

        }

    }

}

