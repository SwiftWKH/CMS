package com.example.hospitalapitest;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) {

        try {

            // Create RMI Registry
            Registry registry = LocateRegistry.createRegistry(1234);

            // Bind Remote Object
            registry.rebind(
                    "HospitalService",
                    new HospitalServiceImpl()
            );

            System.out.println("==========================================");
            System.out.println(" Hospital Appointment System");
            System.out.println(" RMI Server Started Successfully");
            System.out.println(" Port : 1234");
            System.out.println(" Service Name : HospitalService");
            System.out.println("==========================================");

        } catch (Exception e) {

            System.out.println("Failed to start RMI Server.");

            e.printStackTrace();

        }

    }

}