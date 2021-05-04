/*
  Copyright (C) 2021 Barry DeZonia

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published by
  the Free Software Foundation; either version 2.1 of the License, or (at
  your option) any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
  General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package nom.bdezonia.zorbage.ecat;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import nom.bdezonia.zorbage.algebra.Allocatable;
import nom.bdezonia.zorbage.algebra.G;
import nom.bdezonia.zorbage.algorithm.GridIterator;
import nom.bdezonia.zorbage.data.DimensionedDataSource;
import nom.bdezonia.zorbage.data.DimensionedStorage;
import nom.bdezonia.zorbage.misc.DataBundle;
import nom.bdezonia.zorbage.sampling.IntegerIndex;
import nom.bdezonia.zorbage.sampling.SamplingIterator;
import nom.bdezonia.zorbage.type.integer.int16.SignedInt16Member;
import nom.bdezonia.zorbage.type.integer.int32.SignedInt32Member;
import nom.bdezonia.zorbage.type.integer.int8.UnsignedInt8Member;
import nom.bdezonia.zorbage.type.real.float32.Float32Member;
import nom.bdezonia.zorbage.type.real.float64.Float64Member;

/**
 * 
 * @author Barry DeZonia
 *
 */
@SuppressWarnings({"rawtypes", "unused", "unchecked"})
public class Ecat {
	
	// NOTE: so far headers are all from ecat 7. I can also make it support ecat 6 with more work.
	
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

			String magicNumber = readString(data, 14);
			String fname = readString(data, 32);
			short swVersion = readShort(data, false);
			short systemType = readShort(data, false);
			short fileType = readShort(data, false);
			// TODO: I may have read that the header is always big endian so this code might not work
			if (fileType < 0 || fileType > 127) {
				fileIsBigEndian = true;
				swVersion = swapShort(swVersion);
				systemType = swapShort(systemType);
				fileType = swapShort(fileType);
			}
			String serialNumber = readString(data, 10);
			int scanStartTime = readInt(data, fileIsBigEndian);
			String isotopeName = readString(data, 8);
			float isotopeHalflife = readFloat(data, fileIsBigEndian);
			String radiopharmaceutical = readString(data, 32);
			float gantryTilt = readFloat(data, fileIsBigEndian);
			float gantryRotation = readFloat(data, fileIsBigEndian);
			float bedElevation = readFloat(data, fileIsBigEndian);
			float intrinsicTilt = readFloat(data, fileIsBigEndian);
			short wobbleSpeed = readShort(data, fileIsBigEndian);
			short transmissionSourceType = readShort(data, fileIsBigEndian);
			float distanceScanned = readFloat(data, fileIsBigEndian);
			float transaxialFOV = readFloat(data, fileIsBigEndian);
			short angularCompression = readShort(data, fileIsBigEndian);
			short coinSampleMode = readShort(data, fileIsBigEndian);
			short axialSampleMode = readShort(data, fileIsBigEndian);
			float ecatCalibrationFactor = readFloat(data, fileIsBigEndian);
			short calibrationUnits = readShort(data, fileIsBigEndian);
			short calibrationUnitsLabel = readShort(data, fileIsBigEndian);
			short compressionCode = readShort(data, fileIsBigEndian);
			String studyType = readString(data, 12);
			String patientId = readString(data, 16);
			String patientName = readString(data, 32);
			String patientSex = readString(data, 1);
			String patientDexterity = readString(data, 1);
			float patientAge = readFloat(data, fileIsBigEndian);
			float patientHeight = readFloat(data, fileIsBigEndian);
			float patientWeight = readFloat(data, fileIsBigEndian);
			int patientBirthDate = readInt(data, fileIsBigEndian);
			String physicianName = readString(data, 32);
			String operatorName = readString(data, 32);
			String studyDescription = readString(data, 32);
			short acquisitionType = readShort(data, fileIsBigEndian);
			short patientOrientation = readShort(data, fileIsBigEndian);
			String facilityName = readString(data, 20);
			short numPlanes = readShort(data, fileIsBigEndian);
			short numFrames = readShort(data, fileIsBigEndian);
			short numGates = readShort(data, fileIsBigEndian);
			short numBedPositions = readShort(data, fileIsBigEndian);
			float bedPosition0 = readFloat(data, fileIsBigEndian);
			float bedPosition1 = readFloat(data, fileIsBigEndian);
			float bedPosition2 = readFloat(data, fileIsBigEndian);
			float bedPosition3 = readFloat(data, fileIsBigEndian);
			float bedPosition4 = readFloat(data, fileIsBigEndian);
			float bedPosition5 = readFloat(data, fileIsBigEndian);
			float bedPosition6 = readFloat(data, fileIsBigEndian);
			float bedPosition7 = readFloat(data, fileIsBigEndian);
			float bedPosition8 = readFloat(data, fileIsBigEndian);
			float bedPosition9 = readFloat(data, fileIsBigEndian);
			float bedPosition10 = readFloat(data, fileIsBigEndian);
			float bedPosition11 = readFloat(data, fileIsBigEndian);
			float bedPosition12 = readFloat(data, fileIsBigEndian);
			float bedPosition13 = readFloat(data, fileIsBigEndian);
			float bedPosition14 = readFloat(data, fileIsBigEndian);
			float bedPosition15 = readFloat(data, fileIsBigEndian);
			float planeSeparation = readFloat(data, fileIsBigEndian);
			short lwrSctrThresh = readShort(data, fileIsBigEndian);
			short lwrTrueThresh = readShort(data, fileIsBigEndian);
			short uprTrueThresh = readShort(data, fileIsBigEndian);
			String userProcessCode = readString(data, 10);
			short acquisitionMode = readShort(data, fileIsBigEndian);
			float binSize = readFloat(data, fileIsBigEndian);
			float branchingFraction = readFloat(data, fileIsBigEndian);
			int doseStartTime = readInt(data, fileIsBigEndian);
			float dosage = readFloat(data, fileIsBigEndian);
			float wellCounterCorrFactor = readFloat(data, fileIsBigEndian);
			String dataUnits = readString(data, 32);
			short septaState = readShort(data, fileIsBigEndian);
			short[] fillA = new short[6];
			for (int i = 0; i < fillA.length; i++) {
				fillA[i] = readShort(data, fileIsBigEndian);
			}
			
			// TODO read matrix list
			
			for (int f = 0; f < numFrames; f++) {
				for (int p = 0; p < numPlanes; p++) {
					for (int g = 0; g < numGates; g++) {
						for (int b = 0; b < numBedPositions; b++) {
							// TODO
						}
					}
				}
			}
			long[] dims;
			short dataType;
			short numDimensions;
			int frameDuration;
			short numAngles;
			float numAnglesF;  // TODO: view a spec: this looks wrong
			short numRElements;
			float numRElementsF;  // TODO: view a spec: this looks wrong
			int frameStartTime;
			int gateDuration;
			int rWaveOffset;
			int numAcceptedBeats;
			short ringDifference;
			short span;
			short storageOrder;
			float scaleFactor;
			float xOffset, yOffset, zOffset;
			float xResolution, yResolution, zResolution, wResolution;
			short[] fillUser;
			
			switch (fileType) {
			
			case 3:  // attenuation data
				
				dataType = readShort(data, fileIsBigEndian);
				numDimensions = readShort(data, fileIsBigEndian);
				dims = new long[numDimensions];
				short attenType = readShort(data, fileIsBigEndian); // Added TJB 20170223
				numRElements = readShort(data, fileIsBigEndian);
				numAngles = readShort(data, fileIsBigEndian);   
				short numZElements = readShort(data, fileIsBigEndian);
				if (numDimensions > 0)
					dims[0] = numRElements;
				if (numDimensions > 1)
					dims[1] = numAngles;
				if (numDimensions > 2)
					dims[2] = numZElements;
				for (int i = 3; i < numDimensions; i++) {
					dims[i] = 1;
				}
				ringDifference = readShort(data, fileIsBigEndian);
				xResolution = readFloat(data, fileIsBigEndian);
				yResolution = readFloat(data, fileIsBigEndian);
				zResolution = readFloat(data, fileIsBigEndian);
				wResolution = readFloat(data, fileIsBigEndian);
				scaleFactor = readFloat(data, fileIsBigEndian);
				xOffset = readFloat(data, fileIsBigEndian);
				yOffset = readFloat(data, fileIsBigEndian);
				float xRadius = readFloat(data, fileIsBigEndian);
				float yRadius = readFloat(data, fileIsBigEndian);
				float tiltAngle = readFloat(data, fileIsBigEndian);
				float attenuationCoeff = readFloat(data, fileIsBigEndian);
				float attenuationMin = readFloat(data, fileIsBigEndian);
				float attenuationMax = readFloat(data, fileIsBigEndian);
				float skullThickness = readFloat(data, fileIsBigEndian);
				short numAdditionalAttenCoeff = readShort(data, fileIsBigEndian);
				float[] additionalAttenCoeff = new float[8];
				for (int i = 0; i < additionalAttenCoeff.length; i++) {
					additionalAttenCoeff[i] = readFloat(data, fileIsBigEndian);
				}
				float edgeFindingThreshold = readFloat(data, fileIsBigEndian);
				storageOrder = readShort(data, fileIsBigEndian);
				span = readShort(data, fileIsBigEndian);
				short[] zElements = new short[64];
				for (int i = 0; i < zElements.length; i++) {
					zElements[i] = readShort(data, fileIsBigEndian);
				}
				short[] fillUnused = new short[86];
				for (int i = 0; i < fillUnused.length; i++) {
					fillUnused[i] = readShort(data, fileIsBigEndian);
				}
				fillUser = new short[50];
				for (int i = 0; i < fillUser.length; i++) {
					fillUser[i] = readShort(data, fileIsBigEndian);
				}
				break;
				
			case 7:  // image data
			
				dataType = readShort(data, fileIsBigEndian);
				numDimensions = readShort(data, fileIsBigEndian);
				dims = new long[numDimensions];
				short xDimension = readShort(data, fileIsBigEndian);
				short yDimension = readShort(data, fileIsBigEndian);
				short zDimension = readShort(data, fileIsBigEndian);
				if (numDimensions > 0)
					dims[0] = xDimension;
				if (numDimensions > 1)
					dims[1] = yDimension;
				if (numDimensions > 2)
					dims[2] = zDimension;
				for (int i = 3; i < numDimensions; i++) {
					dims[i] = 1;
				}
				xOffset = readFloat(data, fileIsBigEndian);
				yOffset = readFloat(data, fileIsBigEndian);
				zOffset = readFloat(data, fileIsBigEndian);
				float reconZoom = readFloat(data, fileIsBigEndian);
				scaleFactor = readFloat(data, fileIsBigEndian);
				short imageMin = readShort(data, fileIsBigEndian);
				short imageMax = readShort(data, fileIsBigEndian);
				float xPixelSize = readFloat(data, fileIsBigEndian);
				float yPixelSize = readFloat(data, fileIsBigEndian);
				float zPixelSize = readFloat(data, fileIsBigEndian);
				frameDuration = readInt(data, fileIsBigEndian);
				frameStartTime = readInt(data, fileIsBigEndian);
				short filterCode = readShort(data, fileIsBigEndian);
				xResolution = readFloat(data, fileIsBigEndian);
				yResolution = readFloat(data, fileIsBigEndian);
				zResolution = readFloat(data, fileIsBigEndian);
				numRElementsF = readFloat(data, fileIsBigEndian);
				numAnglesF = readFloat(data, fileIsBigEndian);
				float zRotationAngle = readFloat(data, fileIsBigEndian);
				float decayCorrFctr = readFloat(data, fileIsBigEndian);
				int processingCode = readInt(data, fileIsBigEndian);  // % see interpCodes(sh.processing_code) function below
				gateDuration = readInt(data, fileIsBigEndian);
				rWaveOffset = readInt(data, fileIsBigEndian);
				numAcceptedBeats = readInt(data, fileIsBigEndian);
				float filterCutoffFrequency = readFloat(data, fileIsBigEndian);
				float filterResolution = readFloat(data, fileIsBigEndian);
				float filterRampSlope = readFloat(data, fileIsBigEndian);
				short filterOrder = readShort(data, fileIsBigEndian);
				float filterScatterFraction = readFloat(data, fileIsBigEndian);
				float filterScatterSlope = readFloat(data, fileIsBigEndian);
				String annotation = readString(data, 40);
				float m_1_1 = readFloat(data, fileIsBigEndian);
				float m_1_2 = readFloat(data, fileIsBigEndian);
				float m_1_3 = readFloat(data, fileIsBigEndian);
				float m_2_1 = readFloat(data, fileIsBigEndian);
				float m_2_2 = readFloat(data, fileIsBigEndian);
				float m_2_3 = readFloat(data, fileIsBigEndian);
				float m_3_1 = readFloat(data, fileIsBigEndian);
				float m_3_2 = readFloat(data, fileIsBigEndian);
				float m_3_3 = readFloat(data, fileIsBigEndian);
				float rfilterCutoff = readFloat(data, fileIsBigEndian);
				float rfilterResolution = readFloat(data, fileIsBigEndian);
				short rfilterCode = readShort(data, fileIsBigEndian);
				short rfilterOrder = readShort(data, fileIsBigEndian);
				float zfilterCutoff = readFloat(data, fileIsBigEndian);
				float zfilterResolution = readFloat(data, fileIsBigEndian);
				short zfilterCode = readShort(data, fileIsBigEndian);
				short zfilterOrder = readShort(data, fileIsBigEndian);
				float m_1_4 = readFloat(data, fileIsBigEndian);
				float m_2_4 = readFloat(data, fileIsBigEndian);
				float m_3_4 = readFloat(data, fileIsBigEndian);
				short scatterType = readShort(data, fileIsBigEndian);
				short reconType = readShort(data, fileIsBigEndian);
				short reconViews = readShort(data, fileIsBigEndian);
				short[] fillCti = new short[87];
				for (int i = 0; i < fillCti.length; i++) {
					fillCti[i] = readShort(data, fileIsBigEndian);
				}
				fillUser = new short[49];
				for (int i = 0; i < fillUser.length; i++) {
					fillUser[i] = readShort(data, fileIsBigEndian);
				}
				break;
			
			case 11:  // 3d scan (sinogram) data file
				
				dataType = readShort(data, fileIsBigEndian);
				numDimensions = readShort(data, fileIsBigEndian);
				numRElements = readShort(data, fileIsBigEndian);
				numAngles = readShort(data, fileIsBigEndian);
				short correctionsApplied = readShort(data, fileIsBigEndian);
				numZElements = readShort(data, fileIsBigEndian);
				dims = new long[numDimensions];
				if (numDimensions > 0)
					dims[0] = numRElements;
				if (numDimensions > 1)
					dims[1] = numAngles;
				if (numDimensions > 2)
					dims[2] = numZElements;
				for (int i = 3; i < numDimensions; i++) {
					dims[i] = 1;
				}
				ringDifference = readShort(data, fileIsBigEndian);
				xResolution = readFloat(data, fileIsBigEndian);
				yResolution = readFloat(data, fileIsBigEndian);
				zResolution = readFloat(data, fileIsBigEndian);
				wResolution = readFloat(data, fileIsBigEndian);
				short[] fill = new short[6];
				for (int i = 0; i < fill.length; i++) {
					fill[i] = readShort(data, fileIsBigEndian);
				}
				gateDuration = readInt(data, fileIsBigEndian); // TODO: make unsigned
				rWaveOffset = readInt(data, fileIsBigEndian);
				numAcceptedBeats = readInt(data, fileIsBigEndian);
				scaleFactor = readFloat(data, fileIsBigEndian);
				short scanMin = readShort(data, fileIsBigEndian);
				short scanMax = readShort(data, fileIsBigEndian);
				int prompts = readInt(data, fileIsBigEndian);
				int delayed = readInt(data, fileIsBigEndian);
				int multiples = readInt(data, fileIsBigEndian);
				int netTrues = readInt(data, fileIsBigEndian);
				float[] corSingles = new float[16];
				for (int i = 0; i < corSingles.length; i++) {
					corSingles[i] = readFloat(data, fileIsBigEndian);
				}
				float[] uncorSingles = new float[16];
				for (int i = 0; i < uncorSingles.length; i++) {
					uncorSingles[i] = readFloat(data, fileIsBigEndian);
				}
				float totAvgCor = readFloat(data, fileIsBigEndian);
				float totAvgUncor = readFloat(data, fileIsBigEndian);
				int totCoinRate = readInt(data, fileIsBigEndian);
				frameStartTime = readInt(data, fileIsBigEndian);
				frameDuration = readInt(data, fileIsBigEndian);
				float lossCorrectionFactor = readFloat(data, fileIsBigEndian);
				int[] physicalPlanes = new int[8];
				for (int i = 0; i < physicalPlanes.length; i++) {
					physicalPlanes[i] = readInt(data, fileIsBigEndian);
				}
				break;
			
			case 13:  // 3d normalization
			
				dataType = readShort(data, fileIsBigEndian);
				numDimensions = readShort(data, fileIsBigEndian);
				numRElements = readShort(data, fileIsBigEndian);
				numAngles = readShort(data, fileIsBigEndian);
				numZElements = readShort(data, fileIsBigEndian);
				dims = new long[numDimensions];
				if (numDimensions > 0)
					dims[0] = numRElements;
				if (numDimensions > 1)
					dims[1] = numAngles;
				if (numDimensions > 2)
					dims[2] = numZElements;
				for (int i = 3; i < numDimensions; i++) {
					dims[i] = 1;
				}
				ringDifference = readShort(data, fileIsBigEndian);
				scaleFactor = readFloat(data, fileIsBigEndian);
				float normMin = readFloat(data, fileIsBigEndian);
				float normMax = readFloat(data, fileIsBigEndian);
				float fov_source_width = readFloat(data, fileIsBigEndian);
				float norm_quality_factor = readFloat(data, fileIsBigEndian);
				short norm_quality_factor_code = readShort(data, fileIsBigEndian);
				storageOrder = readShort(data, fileIsBigEndian);
				span = readShort(data, fileIsBigEndian);
				zElements = new short[64];
				for (int i = 0; i < zElements.length; i++) {
					zElements[i] = readShort(data, fileIsBigEndian);
				}
				fillCti = new short[123];
				for (int i = 0; i < fillCti.length; i++) {
					fillCti[i] = readShort(data, fileIsBigEndian);
				}
				fillUser = new short[50];
				for (int i = 0; i < fillUser.length; i++) {
					fillUser[i] = readShort(data, fileIsBigEndian);
				}
				break;
			
			default:
			
				System.out.println("ECAT: unknown file type ("+fileType+") : no data was read!");
				return new DataBundle();
			}
			
			Allocatable type = value(dataType);
			
			DimensionedDataSource d = DimensionedStorage.allocate(type, dims);
			
			SamplingIterator<IntegerIndex> iter = GridIterator.compute(dims);
			IntegerIndex idx = new IntegerIndex(dims.length);
			while (iter.hasNext()) {
				iter.next(idx);
				readValue(data, dataType, fileIsBigEndian, type);
				d.set(idx, type);
			}
			
		} catch (IOException e) {
			try {
				if (data != null)
					data.close();
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

	private static Allocatable value(short dataType) {
		switch (dataType) {
		case 1: // byte
			return G.UINT8.construct();
		case 2: // short : VAX_I2 LITTLE
			return G.INT16.construct();
		case 3: // int : VAX_I4 LITTLE
			return G.INT32.construct();
		case 4: // double : VAX_R4 LITTLE
			return G.DBL.construct();
		case 5: // float : IEEE FLT
			return G.FLT.construct();
		case 6: // short : SUN_I2 BIG
			return G.INT16.construct();
		case 7: // int : SUN_I4 big
			return G.INT32.construct();
		default:
			throw new IllegalArgumentException("Unknown data type! "+dataType);
		}
	}

	private static void readValue(DataInputStream d, short dataType, boolean fileIsBigEndian, Allocatable type) throws IOException {
		byte tb;
		short ts;
		int ti;
		float tf;
		double td;

		switch (dataType) {
		case 1: // byte
			tb = readByte(d);
			((UnsignedInt8Member) type).setV(tb);
		case 2: // short : VAX_I2 LITTLE
			ts = readVaxI2(d, fileIsBigEndian);
			((SignedInt16Member) type).setV(ts);
			break;
		case 3: // int : VAX_I4 LITTLE
			ti = readVaxI4(d, fileIsBigEndian);
			((SignedInt32Member) type).setV(ti);
			break;
		case 4: // double : VAX_R4 LITTLE
			td = readVaxR4(d, fileIsBigEndian);
			((Float64Member) type).setV(td);
			break;
		case 5: // float : IEEE FLT
			tf = readIeeeR4(d, fileIsBigEndian);
			((Float32Member) type).setV(tf);
			break;
		case 6: // short : SUN_I2 BIG
			ts = readSunI2(d, fileIsBigEndian);
			((SignedInt16Member) type).setV(ts);
			break;
		case 7: // int : SUN_I4 big
			ti = readSunI4(d, fileIsBigEndian);
			((SignedInt32Member) type).setV(ti);
			break;
		default:
			throw new IllegalArgumentException("Unknown data type! "+dataType);
		}

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

	private static short readVaxI2(DataInputStream str, boolean fileIsBigEndian) throws IOException {
		short v = str.readShort();
		if (fileIsBigEndian) v = swapShort(v);
		return v;
	}

	private static int readVaxI4(DataInputStream str, boolean fileIsBigEndian) throws IOException {
		int v = str.readInt();
		if (fileIsBigEndian) v = swapInt(v);
		return v;
	}

	// NOTE: to preserve the full accuracy of the vax float returning it as a double
	
	private static double readVaxR4(DataInputStream str, boolean fileIsBigEndian) throws IOException {
		int bits = str.readInt();
		if (fileIsBigEndian) {
			//swap words
			short lo = (short) ((bits >>  0) & 0xffff);
			short hi = (short) ((bits >> 16) & 0xffff);
			bits = (lo << 16) | (hi << 0);
		}
		return vaxFloatBitsToDouble(bits);
	}

	private static short readSunI2(DataInputStream str, boolean fileIsBigEndian) throws IOException {
		short v = str.readShort();
		if (!fileIsBigEndian) v = swapShort(v);
		return v;
	}

	private static int readSunI4(DataInputStream str, boolean fileIsBigEndian) throws IOException {
		int v = str.readInt();
		if (!fileIsBigEndian) v = swapInt(v);
		return v;
	}

	private static float readIeeeR4(DataInputStream str, boolean fileIsBigEndian) throws IOException {
		return readFloat(str, fileIsBigEndian);
	}
	
	private static double vaxFloatBitsToDouble(int bits) {

		// format outlined here
		//   http://www.turkupetcentre.net/petanalysis/format_image_ecat.html
		// what I don't have info on: infs/nans/subnormals

		int sign = (bits >> 31) & 1;
		int exponent = (bits >> 23) & 0xff;
		int mantissa = (bits >> 0) & ((1<<24) - 1);
		
		double value = (1.0 + (mantissa / Math.pow(2, 23))) * Math.pow(2, exponent - 129);

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
	
	public static void main(String[] args) {
		DataBundle data = Ecat.load("/home/bdz/Desktop/099_S_2146_881_2be9_de11.v");
	}
}
