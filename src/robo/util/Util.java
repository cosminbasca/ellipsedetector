/*
 * Copyright 2005, 2009 Cosmin Basca.
 * e-mail: cosmin.basca@gmail.com
 * 
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package robo.util;

import java.text.DateFormat;
import java.util.Calendar;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public final class Util 
{
	private Util()
	{
	}
	
	public static byte[] append(byte arr1[],byte arr2[],int offset,int length)
	{
		if(length>(arr2.length-offset) )
		{
			return arr1;
		}
		
		if(arr1 == null || arr1.length==0)
		{
			arr1 = null;
			arr1 = new byte[length];
			for(int i=0;i<length;i++)
				arr1[i] = arr2[i+offset];
			return arr1;
		}
		
		byte newArray[] = new byte[arr1.length + length];
		for(int i=0;i<arr1.length;i++)
			newArray[i] = arr1[i];
		for(int i=0;i<length;i++)
			newArray[arr1.length + i] = arr2[i+offset];
		
		return newArray;
	}
	
	public static byte[] append(byte arr1[],byte arr2[])
	{
		return Util.append(arr1,arr2,0,arr2.length);
	}
	
	public static byte[] convert(Byte arr[])
	{
		byte array[] = new byte[arr.length];
		
		for(int i=0;i<array.length;i++)
			array[i] = arr[i].byteValue();
		
		return array;
	}
	
	public static short[] convert(Short arr[])
	{
		short array[] = new short[arr.length];
		
		for(int i=0;i<array.length;i++)
			array[i] = arr[i].shortValue();
		
		return array;
	}
	
	public static int[] convert(Integer arr[])
	{
		int array[] = new int[arr.length];
		
		for(int i=0;i<array.length;i++)
			array[i] = arr[i].intValue();
		
		return array;
	}
	
	public static int[] convert(float arr[])
	{
		int array[] = new int[arr.length];
		
		for(int i=0;i<array.length;i++)
			array[i] = (int)arr[i];
		
		return array;
	}
	
	public static int[] convert(double arr[])
	{
		int array[] = new int[arr.length];
		
		for(int i=0;i<array.length;i++)
			array[i] = (int)arr[i];
		
		return array;
	}
	
	public static double[] convertIntToDouble(int arr[])
	{
		double array[] = new double[arr.length];
		
		for(int i=0;i<array.length;i++)
			array[i] = arr[i];
		
		return array;
	}
	
	public static float[] convertIntToFloat(int arr[])
	{
		float array[] = new float[arr.length];
		
		for(int i=0;i<array.length;i++)
			array[i] = arr[i];
		
		return array;
	}
	
	public static int	getDataIndexFromComboBox(JComboBox cbb,String dta)
	{
		for(int i=0;i<((DefaultComboBoxModel)cbb.getModel()).getSize();i++)
		{
			String data = (String)((DefaultComboBoxModel)cbb.getModel()).getElementAt(i);
			if(data.equals(dta))
				return i;
		}
		
		return -1;
	}
	
	public static int getUnsigned(byte b)
	{
		return (int)b & 0xFF;
	}
	
	/*
	public static byte unsignedByte(String s)
	{
		return (byte)Integer.valueOf(s).intValue();
	}
	*/
	
	public static String getCurrentTime()
	{
		return DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
	}
	
	public static void sleep(long nanoseconds)
	{
		try
		{
			Thread.currentThread();
			Thread.sleep(nanoseconds);
		}
		catch(InterruptedException ie)
		{
		}
	}
	
	public static double getHarmonicMean(double vals[])
	{
		double sum 	= 0.0;
		double n	= vals.length;
		for(int i=0;i<vals.length;i++)
			sum += 1.0 / vals[i];
		return n / sum;	
	}
}
