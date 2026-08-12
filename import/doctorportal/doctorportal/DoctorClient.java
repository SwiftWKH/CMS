package com.example.hospitalapitest;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class DoctorClient {
    
    private static DoctorService service;
    private static boolean connected = false;
    
    public static void main(String[] args) {
        try {
            // Try to connect to RMI server
            Registry registry = LocateRegistry.getRegistry("localhost", 1235);
            service = (DoctorService) registry.lookup("DoctorService");
            connected = true;
            
            System.out.println("===========================================");
            System.out.println("Doctor RMI Client Connected Successfully!");
            System.out.println("===========================================");
            System.out.println("Connected to: DoctorService");
            System.out.println("Server Port: 1235");
            System.out.println("Status: Connected");
            System.out.println("===========================================");
            
        } catch (Exception e) {
            System.err.println("===========================================");
            System.err.println("WARNING: Could not connect to RMI Server!");
            System.err.println("Running in OFFLINE mode.");
            System.err.println("Error: " + e.getMessage());
            System.err.println("===========================================");
            connected = false;
        }
        
        // Launch the GUI
        SwingUtilities.invokeLater(() -> {
            try {
                DoctorPortalFrame frame = new DoctorPortalFrame();
                frame.setVisible(true);
                
                // Show connection status
                if (connected) {
                    JOptionPane.showMessageDialog(frame, 
                        "Connected to RMI Server successfully!\n" +
                        "Using real-time data from server.", 
                        "Connection Status", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, 
                        "Running in OFFLINE mode.\n" +
                        "Using sample data.\n" +
                        "Please start the RMI server for full functionality.", 
                        "Connection Status", 
                        JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                System.err.println("Error launching Doctor Portal: " + e.getMessage());
                e.printStackTrace();
                
                JOptionPane.showMessageDialog(null, 
                    "Error launching Doctor Portal:\n" + e.getMessage(), 
                    "Launch Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    public static DoctorService getService() {
        return service;
    }
    
    public static boolean isConnected() {
        return connected;
    }
}