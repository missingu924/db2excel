package com.eoms2.sms.adapter;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiAdapter extends Remote
{

    public abstract void sendSms(String s)
        throws RemoteException;
}
