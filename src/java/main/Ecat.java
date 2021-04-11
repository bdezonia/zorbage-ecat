public class Ecat {
	
	/*
	 * 
	 * ECAT data type:
     *   unknown = 0, byte = 1, vaxi2_little = 2, vaxi4_little = 3,
     *   vaxr4 = 4, float(big?) = 5, suni2_big = 6, suni4_big = 7
     *  (maybe float-big should be last: two sources disagree)
     *  (as it is now is using the best info I have found from multiple sources)
     *  (is byte signed: I don't think so. some sources imply no)
	 * 
	 */ 
	
	public static DataBundle load(String filename) {

		File file1 = new File(filename);
		
		FileInputStream f1 = null;
		
		BufferedInputStream bf1 = null;

		DataInputStream data = null;
				
		boolean fileIsBigEndian = false;
		
		try {
			f1 = new FileInputStream(file1);
			
			bf1 = new BufferedInputStream(f1);
			
			data = new DataInputStream(bf1);

			String magicNumber = readString(data,14);
			String fname = readString(data,32);
			short swVersion = readShort(data,false);
			short systemType = readShort(data,false);
			short fileType = readShort(data,false);
			// TODO: I may have read that the header is always big endian so this code might not work
			if (fileType < 0 || fileType > 127) {
				fileIsBigEndian = true;
				swVersion = swapShort(swVersion);
				systemType = swapShort(systemType);
				fileType = swapShort(fileType);
			}
			String serialNumber = readString(data,10);
			int scanStartTime = readInt(data,fileIsBigEndian);
			String isotopeName = readString(data,8);
			float isotopeHalflife = readFloat(data,fileIsBigEndian);
			String radiopharaceutical = readString(data,32);
			float gantryTilt = readFloat(data,fileIsBigEndian);
			float gantryRotation = readFloat(data,fileIsBigEndian);
			float bedElevation = readFloat(data,fileIsBigEndian);
			float intrinsicTilt = readFloat(data,fileIsBigEndian);
			short wobbleSpeed = readShort(data,fileIsBigEndian);
			short transmissionSourceType = readShort(data,fileIsBigEndian);
			float distanceScanned = readFloat(data,fileIsBigEndian);
			float transaxialFOV = readFloat(data,fileIsBigEndian);
			short angularCompression = readShort(data,fileIsBigEndian);
			float isotopeHalflife = readFloat(data,fileIsBigEndian);
			float isotopeHalflife = readFloat(data,fileIsBigEndian);
			float isotopeHalflife = readFloat(data,fileIsBigEndian);
			float isotopeHalflife = readFloat(data,fileIsBigEndian);
			float isotopeHalflife = readFloat(data,fileIsBigEndian);
			float isotopeHalflife = readFloat(data,fileIsBigEndian);
			short coinSampleMode = readShort(data,fileIsBigEndian);
			short axialSampleMode = readShort(data,fileIsBigEndian);
			float ecatCalibrationFactor = readFloat(data,fileIsBigEndian);
			short calibrationUnits = readShort(data,fileIsBigEndian);
			short calibrationUnitsLabel = readShort(data,fileIsBigEndian);
			short compressionCode = readShort(data,fileIsBigEndian);
			String studType = readString(data,12);
			String patientId = readString(data,16);
			String patientName = readString(data,32);
			String patientSex = readString(data,1);
			String patientDexterity = readString(data,1);
			float patientAge = readFloat(data,fileIsBigEndian);
			float patientHeight = readFloat(data,fileIsBigEndian);
			float patientWeight = readFloat(data,fileIsBigEndian);
			int patientBirthDate = readInt(data,fileIsBigEndian);
			String physicianName = readString(data,32);
			String operatorName = readString(data,32);
			String studyDescription = readString(data,32);
			short acquisitionType = readShort(data,fileIsBigEndian);
			short patientOrientation = readShort(data,fileIsBigEndian);
			String facilityName = readString(data,20);
			short numPlanes = readShort(data,fileIsBigEndian);
			short numFrames = readShort(data,fileIsBigEndian);
			short numGates = readShort(data,fileIsBigEndian);
			short numBedPositions = readShort(data,fileIsBigEndian);
			float bedPosition0 = readFloat(data,fileIsBigEndian);
			float bedPosition1 = readFloat(data,fileIsBigEndian);
			float bedPosition2 = readFloat(data,fileIsBigEndian);
			float bedPosition3 = readFloat(data,fileIsBigEndian);
			float bedPosition4 = readFloat(data,fileIsBigEndian);
			float bedPosition5 = readFloat(data,fileIsBigEndian);
			float bedPosition6 = readFloat(data,fileIsBigEndian);
			float bedPosition7 = readFloat(data,fileIsBigEndian);
			float bedPosition8 = readFloat(data,fileIsBigEndian);
			float bedPosition9 = readFloat(data,fileIsBigEndian);
			float bedPosition10 = readFloat(data,fileIsBigEndian);
			float bedPosition11 = readFloat(data,fileIsBigEndian);
			float bedPosition12 = readFloat(data,fileIsBigEndian);
			float bedPosition13 = readFloat(data,fileIsBigEndian);
			float bedPosition14 = readFloat(data,fileIsBigEndian);
			float bedPosition15 = readFloat(data,fileIsBigEndian);
			float planeSeparation = readFloat(data,fileIsBigEndian);
			short lwrSctrThresh = readShort(data,fileIsBigEndian);
			short lwrTrueThresh = readShort(data,fileIsBigEndian);
			short uprTrueThresh = readShort(data,fileIsBigEndian);
			String userProcessCode = readString(data,10);
			short acquisitionMode = readShort(data,fileIsBigEndian);
			float binSize = readFloat(data,fileIsBigEndian);
			float branchingFraction = readFloat(data,fileIsBigEndian);
			int doseStartTime = readInt(data,fileIsBigEndian);
			float dosage = readFloat(data,fileIsBigEndian);
			float wellCounterCorrFactor = readFloat(data,fileIsBigEndian);
			String dataUnits = readString(data,32);
			short septaState = readShort(data,fileIsBigEndian);
			short fill0 = readShort(data,fileIsBigEndian);
			short fill1 = readShort(data,fileIsBigEndian);
			short fill2 = readShort(data,fileIsBigEndian);
			short fill3 = readShort(data,fileIsBigEndian);
			short fill4 = readShort(data,fileIsBigEndian);
			short fill5 = readShort(data,fileIsBigEndian);
		} catch (IOException e) {
			try {
				if (data != null)
					data.close()
				else if (bf1 != null)
					bf1.close();
				else if (f1 != null)
					f1.close();
			} catch (Exception ex) {
			    System.out.println("Err 2: " + ex);
			}
			System.out.println("Err 1: " + e);
		}
		return new DataBundle();
	}

	private static byte readByte(DataInputStream str) throws IOException {
		return str.readByte();
	}
	
	private static short readShort(DataInputStream str, boolean fileIsBigEndian) throws IOException {
		short v = str.readShort();
		if (fileIsBigEndian) v = swapShort(v);
		return v;
	}
	
	private static int readInt(DataInputStream str, boolean fileIsBigEndian) throws IOException {
		int v = str.readInt();
		if (fileIsBigEndian) v = swapInt(v);
		return v;
	}
	
	private static float readFloat(DataInputStream str, boolean fileIsBigEndian) throws IOException {
		if (fileIsBigEndian) {
			int bits = str.readInt();
			bits = swapInt(bits);
			return Float.intBitsToFloat(bits);
		}
		return str.readFloat();
	}
	
	private static String readString(DataInputStream d, int maxChars) throws IOException {
		StringBuilder str = new StringBuilder();
		boolean done = false;
		for (int i = 0; i < maxChars; i++) {
			char ch = (char) readByte(d);
			if (ch == 0) {
				done = true;
			}
			if (!done) {
				str.append(ch);
			}
		}
		return str.toString();
	}

	private static short readVaxI2(DataInputStream str, boolean fileIsBigEndian) {
		short v = str.readShort();
		if (fileIsBigEndian) v = swapShort(v);
		return v;
	}

	private static int readVaxI4(DataInputStream str, boolean fileIsBigEndian) {
		int v = str.readInt();
		if (fileIsBigEndian) v = swapInt(v);
		return v;
	}

	private static double readVaxR4(DataInputStream str, boolean fileIsBigEndian) {
		int bits = str.readInt();
		if (fileIsBigEndian) {
			//swap words
			short lo = (bits >>  0) & 0xffff;
			short hi = (bits >> 16) & 0xffff;
			bits = (lo << 16) | (hi << 0);
		}
		return vaxFloatBitsToDouble(bits);
	}

	private static short readSunI2(DataInputStream str, boolean fileIsBigEndian) {
		short v = str.readShort();
		if (!fileIsBigEndian) v = swapShort(v);
		return v;
	}

	private static int readSunI4(DataInputStream str, boolean fileIsBigEndian) {
		int v = str.readInt();
		if (!fileIsBigEndian) v = swapInt(v);
		return v;
	}

	private static float readIeeeR4(DataInputStream str, boolean fileIsBigEndian) {
		return readFloat(str, fileIsBigEndian);
	}
	
	private static double vaxFloatBitsToDouble(int bits) {

		// format outlined here
		//   http://www.turkupetcentre.net/petanalysis/format_image_ecat.html
		// what I don't have info on: infs/nans/subnormals

		int sign = (bits >> 31) & 1;
		int exponent = (bits >> 23) & 0xff;
		int mantissa = (bits >> 0) & (2^23 - 1);
		
		double value = (1.0 + ((1.0 * mantissa) / 2^23)) * Math.pow(2, exponent - 129);

		if (sign == 0)
			return value;
		
		return -value;
	}

	private static short swapShort(short in) {
		int b0 = (in >> 0) & 0xff;
		int b1 = (in >> 8) & 0xff;
		return (short) ((b0 << 8) | (b1 << 0));
	}
	
	private static int swapInt(int in) {
		int b0 = (in >> 0) & 0xff;
		int b1 = (in >> 8) & 0xff;
		int b2 = (in >> 16) & 0xff;
		int b3 = (in >> 24) & 0xff;
		return (b0 << 24) | (b1 << 16) | (b2 << 8) | (b3 << 0);
	}
}
