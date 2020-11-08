/* 
 * Copyright (C) 2015 SDN-WISE
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.sdnwiselab.sdnwise.loader;

import com.github.sdnwiselab.sdnwise.configuration.Configurator;
import com.github.sdnwiselab.sdnwise.controller.Controller;
import com.github.sdnwiselab.sdnwise.controller.ControllerFactory;
import com.github.sdnwiselab.sdnwise.packet.DataPacket;
import com.github.sdnwiselab.sdnwise.util.CipherUtils;
import com.github.sdnwiselab.sdnwise.util.MultiChainUtils;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.logging.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SdnWise class of the SDN-WISE project. It loads the configuration file and
 * starts the the Controller.
 *
 * @author Sebastiano Milardo
 * @version 0.1
 */
public class SdnWise {

    /**
     * Starts the components of the SDN-WISE Controller.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        new SdnWise().startExample();
    }

    private Controller controller;
    Logger MeasureLOGGER;

    /**
     * Starts the Controller layer of the SDN-WISE network. The path to the
     * configurations are specified in the configFilePath String. The options to
     * be specified in this file are: a "lower" Adapter, in order to communicate
     * with the flowVisor (See the Adapter javadoc for more info), an
     * "algorithm" for calculating the shortest path in the network. The only
     * supported at the moment is "DIJKSTRA". A "map" which contains
     * informations regarding the "TIMEOUT" in order to remove a non responding
     * node from the topology, a "RSSI_RESOLUTION" value that triggers an event
     * when a link rssi value changes more than the set threshold.
     *
     * @param configFilePath a String that specifies the path to the
     * configuration file.
     * @return the Controller layer of the current SDN-WISE network.
     */
    public Controller startController(String configFilePath) {
        InputStream configFileURI = null;
        if (configFilePath == null || configFilePath.isEmpty()) {
            configFileURI = this.getClass().getResourceAsStream("/config.ini");
        } else {
            try {
                configFileURI = new FileInputStream(configFilePath);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SdnWise.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Configurator conf = Configurator.load(configFileURI);
        controller = new ControllerFactory().getController(conf.getController());
        new Thread(controller).start();
        return controller;
    }


    public void startExample() {
    	MeasureLOGGER = Logger.getLogger("Measure0.1");
        MeasureLOGGER.setLevel(Level.parse("FINEST"));
        try {
            FileHandler fh;
            File dir = new File("logs");
            dir.mkdir();
            fh = new FileHandler("logs/Measures0.1-GetStarted.log");
            fh.setFormatter(new SimpleFormatter());
            MeasureLOGGER.addHandler(fh);
            MeasureLOGGER.setUseParentHandlers(false);
        } catch (IOException | SecurityException ex) {
        }
        
        controller = startController("");

        System.out.println("SDN-WISE Controller running....");
        
        // We wait for the network to start 
        try {
            Thread.sleep(10000);	//Initially: 60000
        
            // Then we query the nodes
            while (true){    
                for (int i = 1; i < 12; i++){	//Initially: 12
                    System.out.println("- quering node " + i);
                    int netId = 1;
                    NodeAddress dst = new NodeAddress(i);
                    NodeAddress src = new NodeAddress(1);
                    
                    PrivateKey priKey512 = CipherUtils.getPrivateKey("/home/sarmad/contiki/tools/cooja/build/keys/1-512.key");
                    String token = UUID.randomUUID().toString();
                    //Random random = new Random();
                    //String token = String.valueOf(random.nextInt(1000000));
                    //System.out.println(token);
                    String tokenHash = CipherUtils.hash(token);
                    //System.out.println(tokenHash);
                    String encryptedTokenHash = CipherUtils.encrypt(tokenHash, priKey512);
                    //System.out.println(encryptedTokenHash);
                    String text = "{ \"message\": \"Hello World! From 1 to " + i + "\", \"token\": \"" + token + "\", \"signature\": \"" + encryptedTokenHash + "\" }";
                    //System.out.println(text);
                    //System.out.println(text.getBytes("UTF-8").length);
                    PrivateKey priKey2048 = CipherUtils.getPrivateKey("/home/sarmad/contiki/tools/cooja/build/keys/1-2048.key");
                    long encryptAndSendMessageStartTime = System.currentTimeMillis();
                    String encryptedText = CipherUtils.encrypt(text, priKey2048);
                    System.out.println(encryptedText);
                    
                    DataPacket p = new DataPacket(netId,src,dst);
                    p.setNxhop(src);
                    //p.setPayloadSize(106);
                    byte[] payloadBytes = Base64.getDecoder().decode(encryptedText);
                    //System.out.println(payloadBytes.length);
                    //p.setPayload(payloadBytes);
                    p.setPayload(payloadBytes, 0, 0, 64);
                    controller.sendNetworkPacket(p);
                    Thread.sleep(100);
                    p.setPayload(payloadBytes, 64, 0, 64);
                    controller.sendNetworkPacket(p);
                    Thread.sleep(100);
                    p.setPayload(payloadBytes, 128, 0, 64);
                    controller.sendNetworkPacket(p);
                    Thread.sleep(100);
                    p.setPayload(payloadBytes, 192, 0, 64);
                    controller.sendNetworkPacket(p);
                    Thread.sleep(100);
                    
                    payloadBytes = "EOP".getBytes("UTF-8");
                    p.setPayload(payloadBytes);
                    controller.sendNetworkPacket(p);
                    myLog("Encrypt and send message", encryptAndSendMessageStartTime);
                    Thread.sleep(2000);
                }
            }
        
        } catch (InterruptedException ex) {
            Logger.getLogger(SdnWise.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
        	Logger.getLogger(SdnWise.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
        	Logger.getLogger(SdnWise.class.getName()).log(Level.SEVERE, null, ex);
    	}
    }
    
    void myLog(String operation, long startTime) {
        MeasureLOGGER.log(Level.FINEST,
                "{0}|{1}|{2}|{3}",
                new Object[]{
                    operation,
                    startTime,
                    System.currentTimeMillis(),
                    new Date().toString()
                });
    }
}
