package org.faas.utils.math;

//Java program to find mean 
//and median of an array
import java.util.*;

class DoubleSimpleStatistics
{
 // Function for calculating mean
 public double findMean(double a[], int n)
 {
     int sum = 0;
     for (int i = 0; i < n; i++) 
         sum += a[i];
  
     return (double)sum / (double)n;
 }

 // Function for calculating median
 public double findMedian(double a[], int n)
 {
     // First we sort the array
     Arrays.sort(a);

     // check for even case
     if (n % 2 != 0)
     return (double)a[n / 2];
  
     return (double)(a[(n - 1) / 2] + a[n / 2]) / 2.0;
 }

 // Driver program
 public static void main(String args[])
 {
     double a[] = { 1
    		 ,1.1
    		 ,2
    		 ,2.001
    		 ,2.002
    		 ,3
    		 ,4
    		 ,10
    		 };
     int n = a.length;
     
     DoubleSimpleStatistics stats = new DoubleSimpleStatistics();
     System.out.println("Mean = " + stats.findMean(a, n)); 
     System.out.println("Median = " + stats.findMedian(a, n)); 
 }
}