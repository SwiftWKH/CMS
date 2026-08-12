package com.example.hospitalapitest;

import javax.swing.SwingUtilities;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class client {

    private static HospitalService service;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new recreationmain();

        });

    }

    public static HospitalService getHospitalService() {

        try {

            if (service == null) {

                Registry registry =
                        LocateRegistry.getRegistry(
                                "localhost",
                                1234);

                service =
                        (HospitalService)
                                registry.lookup("HospitalService");

            }

            return service;

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }

}