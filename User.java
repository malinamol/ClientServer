package edu.udo.cs.rvs.ssdp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class User implements Runnable {


    User(){

    }

    public static boolean flag = true;

    public static void stopRunning()
    {
        flag = false;
    }

    /**
     * We read the input from the user and check which case it fits
     */

    @Override
    public void run() {

        while(flag)
        {
            //Enter data using BufferReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PrintStream ps = new PrintStream(System.out);

            // Reading data using readLine
            try {
                String name = reader.readLine();

                switch(name)
                {
                    case "EXIT": {

                        ps.print("Exiting...\n");
                        ps.flush();
                        reader.close();
                        Listen.stopRunning();
                        Worker.stopRunning();
                        this.stopRunning();
                        break;
                        }

                    case "LIST": {

                        if(Worker.storeLinesForPrinting.isEmpty())          //if the list is empty, we wait a little bit to receive content
                            ps.print("Nur ein Moment\n");
                        while(Worker.storeLinesForPrinting.isEmpty()) {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                            }
                        }

                        synchronized (Worker.storeLinesForPrinting)
                        {

                            for(String s: Worker.storeLinesForPrinting)
                            {
                                ps.print(s+"\n");

                            }
                            ps.print("\n");

                        }
                        ps.flush();
                        break;

                    }

                    case "SCAN": {

                        Listen.sendDatagram();
                        ps.print("Datagram sent\n");
                        ps.print("\n");
                        ps.flush();
                        break;
                    }

                    case "CLEAR":{

                        synchronized (Worker.storeLinesForPrinting)
                        {
                            Worker.storeLinesForPrinting.clear();
                            ps.print("All cleared\n");
                            ps.print("\n");
                            ps.flush();
                        }
                        break;
                    }
                }

            } catch (IOException e) {
                System.out.println("Error at reading USER input\n");
            }
        }
    }
}
