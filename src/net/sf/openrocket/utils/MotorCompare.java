package net.sf.openrocket.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.sf.openrocket.file.GeneralMotorLoader;
import net.sf.openrocket.file.MotorLoader;
import net.sf.openrocket.motor.Manufacturer;
import net.sf.openrocket.motor.Motor;
import net.sf.openrocket.motor.ThrustCurveMotor;

public class MotorCompare {
	
	private static final double MAX_THRUST_MARGIN = 0.20;
	private static final double TOTAL_IMPULSE_MARGIN = 0.10;
	private static final double MASS_MARGIN = 0.10;
	
	private static final double THRUST_MARGIN = 0.15;
	
	private static final int DIVISIONS = 100;
	private static final int ALLOWED_INVALID_POINTS = 15;
	
	private static final int MIN_POINTS = 7;

	public static void main(String[] args) throws IOException {
		final double maxThrust;
		final double maxTime;
		int maxDelays;
		int maxPoints;
		int maxCommentLen;

		double min, max;
		double diff;
		
		int[] goodness;
		
		boolean bad = false;
		List<String> cause = new ArrayList<String>();
		
		MotorLoader loader = new GeneralMotorLoader();
		List<Motor> motors = new ArrayList<Motor>();
		List<String> files = new ArrayList<String>();
		
		// Load files
		System.out.printf("Files      :");
		for (String file: args) {
			System.out.printf("\t%s", file);
			List<Motor> m = null;
			try {
				InputStream stream = new FileInputStream(file);
				m = loader.load(stream, file);
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.print("(ERR:" + e.getMessage() + ")");
			}
			if (m != null) {
				motors.addAll(m);
				for (int i=0; i<m.size(); i++)
					files.add(file);
			}
		}
		System.out.println();
		
		if (motors.size() == 0) {
			System.err.println("No motors loaded.");
			System.out.println("ERROR: No motors loaded.\n");
			return;
			
		}
		
		if (motors.size() == 1) {
			System.out.println("Best (ONLY): " + files.get(0));
			System.out.println();
			return;
		}
		
		final int n = motors.size(); 
		goodness = new int[n];
		
		
		// Manufacturers
		System.out.printf("Manufacture:");
		Manufacturer mfg = motors.get(0).getManufacturer();
		for (Motor m: motors) {
			System.out.printf("\t%s", m.getManufacturer());
			if (m.getManufacturer() != mfg) {
				cause.add("Manufacturer");
				bad = true;
			}
		}
		System.out.println();
		
		
		// Max. thrust
		max = 0;
		min = Double.MAX_VALUE;
		System.out.printf("Max.thrust :");
		for (Motor m: motors) {
			double f = m.getMaxThrust();
			System.out.printf("\t%.2f", f);
			max = Math.max(max, f);
			min = Math.min(min, f);
		}
		diff = (max-min)/min;
		if (diff > MAX_THRUST_MARGIN) {
			bad = true;
			cause.add("Max thrust");
		}
		System.out.printf("\t(discrepancy %.1f%%)\n", 100.0*diff);
		maxThrust = (min+max)/2;
		
		
		// Total time
		max = 0;
		min = Double.MAX_VALUE;
		System.out.printf("Total time :");
		for (Motor m: motors) {
			double t = m.getTotalTime();
			System.out.printf("\t%.2f", t);
			max = Math.max(max, t);
			min = Math.min(min, t);
		}
		diff = (max-min)/min;
		System.out.printf("\t(discrepancy %.1f%%)\n", 100.0*diff);
		maxTime = max;
		
		
		// Total impulse
		max = 0;
		min = Double.MAX_VALUE;
		System.out.printf("Impulse    :");
		for (Motor m: motors) {
			double f = m.getTotalImpulse();
			System.out.printf("\t%.2f", f);
			max = Math.max(max, f);
			min = Math.min(min, f);
		}
		diff = (max-min)/min;
		if (diff > TOTAL_IMPULSE_MARGIN) {
			bad = true;
			cause.add("Total impulse");
		}
		System.out.printf("\t(discrepancy %.1f%%)\n", 100.0*diff);
		
		
		// Initial mass
		max = 0;
		min = Double.MAX_VALUE;
		System.out.printf("Init mass  :");
		for (Motor m: motors) {
			double f = m.getMass(0);
			System.out.printf("\t%.2f", f*1000);
			max = Math.max(max, f);
			min = Math.min(min, f);
		}
		diff = (max-min)/min;
		if (diff > MASS_MARGIN) {
			bad = true;
			cause.add("Initial mass");
		}
		System.out.printf("\t(discrepancy %.1f%%)\n", 100.0*diff);
		
		
		// Empty mass
		max = 0;
		min = Double.MAX_VALUE;
		System.out.printf("Empty mass :");
		for (Motor m: motors) {
			double f = m.getMass(Double.POSITIVE_INFINITY);
			System.out.printf("\t%.2f", f*1000);
			max = Math.max(max, f);
			min = Math.min(min, f);
		}
		diff = (max-min)/min;
		if (diff > MASS_MARGIN) {
			bad = true;
			cause.add("Empty mass");
		}
		System.out.printf("\t(discrepancy %.1f%%)\n", 100.0*diff);
		
		
		// Delays
		maxDelays = 0;
		System.out.printf("Delays     :");
		for (Motor m: motors) {
			System.out.printf("\t%d", m.getStandardDelays().length);
			maxDelays = Math.max(maxDelays, m.getStandardDelays().length);
		}
		System.out.println();
		
		
		// Data points
		maxPoints = 0;
		System.out.printf("Points     :");
		for (Motor m: motors) {
			System.out.printf("\t%d", ((ThrustCurveMotor)m).getTimePoints().length);
			maxPoints = Math.max(maxPoints, ((ThrustCurveMotor)m).getTimePoints().length);
		}
		System.out.println();
		
		
		// Comment length
		maxCommentLen = 0;
		System.out.printf("Comment len:");
		for (Motor m: motors) {
			System.out.printf("\t%d", m.getDescription().length());
			maxCommentLen = Math.max(maxCommentLen, m.getDescription().length());
		}
		System.out.println();
		
		
		if (bad) {
			String str = "ERROR: ";
			for (int i=0; i<cause.size(); i++) {
				str += cause.get(i);
				if (i < cause.size()-1)
					str += ", ";
			}
			str += " differs";
			System.out.println(str);
			System.out.println();
			return;
		}
		
		// Check consistency
		int invalidPoints = 0;
		for (int i=0; i < DIVISIONS; i++) {
			double t = maxTime * i/(DIVISIONS-1);
			min = Double.MAX_VALUE;
			max = 0;
//			System.out.printf("%.2f:", t);
			for (Motor m: motors) {
				double f = m.getThrust(t);
//				System.out.printf("\t%.2f", f);
				min = Math.min(min, f);
				max = Math.max(max, f);
			}
			diff = (max-min)/maxThrust;
//			System.out.printf("\t(diff %.1f%%)\n", diff*100);
			if (diff > THRUST_MARGIN)
				invalidPoints++;
		}
		
		if (invalidPoints > ALLOWED_INVALID_POINTS) {
			System.out.println("ERROR: " + invalidPoints + "/" + DIVISIONS 
					+ " points have thrust differing over " + (THRUST_MARGIN*100) + "%");
			System.out.println();
			return;
		}
		
		
		// Check goodness
		for (int i=0; i<n; i++) {
			Motor m = motors.get(i);
			if (m.getStandardDelays().length == maxDelays)
				goodness[i] += 1000;
			if (((ThrustCurveMotor)m).getTimePoints().length == maxPoints)
				goodness[i] += 100;
			if (m.getDescription().length() == maxCommentLen)
				goodness[i] += 10;
			if (files.get(i).matches(".*\\.[rR][sS][eE]$"))
				goodness[i] += 1;
		}
		int best = 0;
		for (int i=1; i<n; i++) {
			if (goodness[i] > goodness[best])
				best = i;
		}
		
		
		// Verify enough points
		int pts = ((ThrustCurveMotor)motors.get(best)).getTimePoints().length;
		if (pts < MIN_POINTS) {
			System.out.println("ERROR: Best has only " + pts + " data points");
			System.out.println();
			return;
		}
		
		System.out.println("Best (" + goodness[best] + "): " + files.get(best));
		System.out.println();
		
		
	}
	
}
