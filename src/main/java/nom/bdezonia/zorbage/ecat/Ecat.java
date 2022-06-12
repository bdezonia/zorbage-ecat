/*
  Copyright (C) 2021-2022 Barry DeZonia

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
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import nom.bdezonia.zorbage.algebra.Allocatable;
import nom.bdezonia.zorbage.algebra.G;
import nom.bdezonia.zorbage.algebra.HighPrecRepresentation;
import nom.bdezonia.zorbage.algorithm.ScaleByDouble;
import nom.bdezonia.zorbage.coordinates.Affine2dCoordinateSpace;
import nom.bdezonia.zorbage.coordinates.Affine3dCoordinateSpace;
import nom.bdezonia.zorbage.coordinates.CoordinateSpace;
import nom.bdezonia.zorbage.coordinates.Cylindrical3dCoordinateSpace;
import nom.bdezonia.zorbage.coordinates.LinearNdCoordinateSpace;
import nom.bdezonia.zorbage.coordinates.Polar2dCoordinateSpace;
import nom.bdezonia.zorbage.data.DimensionedDataSource;
import nom.bdezonia.zorbage.data.DimensionedStorage;
import nom.bdezonia.zorbage.datasource.IndexedDataSource;
import nom.bdezonia.zorbage.misc.DataBundle;
import nom.bdezonia.zorbage.storage.Storage;
import nom.bdezonia.zorbage.type.integer.int16.SignedInt16Member;
import nom.bdezonia.zorbage.type.integer.int16.UnsignedInt16Member;
import nom.bdezonia.zorbage.type.integer.int32.SignedInt32Member;
import nom.bdezonia.zorbage.type.integer.int32.UnsignedInt32Member;
import nom.bdezonia.zorbage.type.integer.int8.SignedInt8Member;
import nom.bdezonia.zorbage.type.integer.int8.UnsignedInt8Member;
import nom.bdezonia.zorbage.type.real.float32.Float32Member;
import nom.bdezonia.zorbage.type.real.float64.Float64Member;
import nom.bdezonia.zorbage.type.real.highprec.HighPrecisionMember;

/**
 * 
 * @author Barry DeZonia
 *
 */
@SuppressWarnings({"rawtypes", "unused", "unchecked"})
public class Ecat {
	
	// NOTE: so far headers are all from ecat 7. I can also make it support ecat 6 with more work.
	
	public static DataBundle loadAllDatasets(String filename) {

		File file1 = new File(filename);
		
		FileInputStream f1 = null;
		
		BufferedInputStream bf1 = null;

		PositionableInputStream c1 = null;

		DataInputStream data = null;
		
		boolean fileIsBigEndian = true;
				
		DataBundle images = new DataBundle();
		
		try {
			f1 = new FileInputStream(file1);
			
			bf1 = new BufferedInputStream(f1);
			
			c1 = new PositionableInputStream(bf1);
			
			data = new DataInputStream(c1);

			String magicNumber = readString(data, 14);
			String fname = readString(data, 32);
			short swVersion = readShort(data, false);
			short systemType = readShort(data, false);
			short fileType = readShort(data, false);

			if (fileType < 0 || fileType > 127) {
				System.out.println("FILE IS LITTLE ENDIAN!!! SWAPPING WILL OCCUR!");
				swVersion = swapShort(swVersion);
				systemType = swapShort(systemType);
				fileType = swapShort(fileType);
				fileIsBigEndian = false;
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
			System.out.println("planes   = " + numPlanes);
			System.out.println("frames   = " + numFrames);
			System.out.println("gates    = " + numGates);
			System.out.println("bedposes = " + numBedPositions);
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
			
			long[] frameAddresses = new long[numFrames];
			int[] matrixNumbers = new int[numFrames];

			// The buffer we will read the directory node bytes into

			byte[] dirNode = new byte[512];

			for (int bedpos = 0; bedpos < Math.max(1, numBedPositions); bedpos++) {
				for (int gate = 0; gate < Math.max(1, numGates); gate++) {
					for (int frame = 0; frame < numFrames; frame++) {
		
						int bucket = frame % 31;
						
						if (bucket == 0) {
							data.read(dirNode);
							ByteArrayInputStream bs = new ByteArrayInputStream(dirNode);
							DataInputStream innerStr = new DataInputStream(bs);
							int numUnused = readInt(innerStr, fileIsBigEndian);
							int nextDirNodeAddress = readInt(innerStr, fileIsBigEndian);
							int prevDirNodeAddress = readInt(innerStr, fileIsBigEndian);
							int numUsed = readInt(innerStr, fileIsBigEndian);
							for (int i = 0; i < 31; i++) {
		
								// some of these will be bogus: we always iterate 31 though
								// this maybe be more than the num frames we need.
								
								int matrixNumber = readInt(innerStr, fileIsBigEndian);
								int subheaderBlockNum = readInt(innerStr, fileIsBigEndian);
								int lastBlock = readInt(innerStr, fileIsBigEndian);
								int status = readInt(innerStr, fileIsBigEndian);
				
								// now record the frame address etc. for valid frames only
								
								if (i < numFrames) {
									frameAddresses[frame+i] = 512*(subheaderBlockNum-1);
									matrixNumbers[frame+i] = matrixNumber;
								}
							}
						}
					}
					
		//			for (int i = 0; i < frameAddresses.length; i++) {
		//				System.out.println("frame " + i + " address = " + frameAddresses[i]);
		//				System.out.println("  matrix number = " + matrixNumbers[i]);
		//			}
					
					for (int f = 0; f < numFrames; f++) {
		
						System.out.println("BEGIN READ FRAME "+f+" AND FILE POS = "+c1.pos);
						
						List<IndexedDataSource<Allocatable>> threeDChunks = new LinkedList<>();
						CoordinateSpace coordSpace = null;
						short dataType = -4000;
						short xDimension = 0, yDimension = 0, zDimension = 0;
						BigDecimal[] scales = new BigDecimal[0];
						BigDecimal[] offsets = new BigDecimal[0];
						String[] axisNames = new String[0];
						IndexedDataSource<Allocatable> frameData = null;
						
						long[] dims = new long[0];
						short numDimensions = 0;
						int frameDuration;
						short numAngles;
						float numAnglesF;  // spec says yes, this is a float
						short numRElements;
						float numRElementsF;  // spec says yes, this is a float
						int frameStartTime;
						int gateDuration;
						int rWaveOffset;
						int numAcceptedBeats;
						short ringDifference;
						short span;
						short storageOrder;
						float scaleFactor = 0;
						float xOffset, yOffset, zOffset;
						float xResolution, yResolution, zResolution, wResolution;
						short[] fillUser;
						boolean signedDataFlag = false;
						
						double rUnit=0, thetaUnit=0, zUnit=0;
						
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
							signedDataFlag = attenuationMin < 0;
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
							rUnit = xResolution;
							thetaUnit = yResolution;
							zUnit = zResolution;
							if (numZElements > 1) {
								coordSpace = new Cylindrical3dCoordinateSpace(
										BigDecimal.valueOf(rUnit),
										BigDecimal.valueOf(thetaUnit),
										BigDecimal.valueOf(zUnit)
										);
								axisNames = new String[3];
								axisNames[0] = "r";
								axisNames[1] = "theta";
								axisNames[2] = "z";
							}
							else {
								coordSpace = new Polar2dCoordinateSpace(
										BigDecimal.valueOf(rUnit),
										BigDecimal.valueOf(thetaUnit)
										);
								axisNames = new String[2];
								axisNames[0] = "r";
								axisNames[1] = "theta";
							}
							break;
							
						case 7:  // image data
						
							dataType = readShort(data, fileIsBigEndian);
							numDimensions = readShort(data, fileIsBigEndian);
							dims = new long[numDimensions];
							xDimension = readShort(data, fileIsBigEndian);
							yDimension = readShort(data, fileIsBigEndian);
							zDimension = readShort(data, fileIsBigEndian);
							if (numDimensions > 0)
								dims[0] = xDimension;
							if (numDimensions > 1)
								dims[1] = yDimension;
							if (numDimensions > 2)
								dims[2] = zDimension;
							for (int i = 3; i < numDimensions; i++) {
								dims[i] = 1;
							}
							System.out.println("data type == " + dataType);
							System.out.println("dims == " + Arrays.toString(dims));
							xOffset = readFloat(data, fileIsBigEndian);
							yOffset = readFloat(data, fileIsBigEndian);
							zOffset = readFloat(data, fileIsBigEndian);
							float reconZoom = readFloat(data, fileIsBigEndian);
							scaleFactor = readFloat(data, fileIsBigEndian);
							short imageMin = readShort(data, fileIsBigEndian);
							signedDataFlag = imageMin < 0;
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
		
							if (coordSpace == null) {
								
								int numLegitDimensions = 0;
								for (int i  = 0; i < numDimensions; i++) {
									if (dims[i] > 1)
										numLegitDimensions++;
								}
		
								if (m_1_1 != 0 || m_1_2 != 0 || m_1_3 != 0 || m_1_4 != 0 || 
										m_2_1 != 0 || m_2_2 != 0 || m_2_3 != 0 || m_2_4 != 0 || 
										m_3_1 != 0 || m_3_2 != 0 || m_3_3 != 0 || m_3_4 != 0)
								{
									if (numLegitDimensions == 2) {
										float u1=0,u2=0,u3=0,v1=0,v2=0,v3=0;
										if (numDimensions == 3) {
											if (xDimension <= 1) {
												u1 = m_2_2;
												u2 = m_2_3;
												u3 = m_2_4;
												v1 = m_3_2;
												v2 = m_3_3;
												v3 = m_3_4;
											}
											else if (yDimension <= 1) {
												u1 = m_1_1;
												u2 = m_1_3;
												u3 = m_1_4;
												v1 = m_3_1;
												v2 = m_3_3;
												v3 = m_3_4;
											}
											else if (zDimension <= 1) {
												u1 = m_1_1;
												u2 = m_1_2;
												u3 = m_1_4;
												v1 = m_2_1;
												v2 = m_2_2;
												v3 = m_2_4;
											}
											else {
												throw new IllegalArgumentException("unexpected num non trivial dimensions");
											}
											coordSpace =
													new Affine2dCoordinateSpace(
															BigDecimal.valueOf(u1), 
															BigDecimal.valueOf(u2),
															BigDecimal.valueOf(u3),
															BigDecimal.valueOf(v1),
															BigDecimal.valueOf(v2),
															BigDecimal.valueOf(v3));
											
										}
										else if (numDimensions == 2) {
											coordSpace =
													new Affine2dCoordinateSpace(
															BigDecimal.valueOf(m_1_1), 
															BigDecimal.valueOf(m_1_2),
															BigDecimal.valueOf(m_1_4),
															BigDecimal.valueOf(m_2_1),
															BigDecimal.valueOf(m_2_2),
															BigDecimal.valueOf(m_2_4));
										}
									}
										
									if (numLegitDimensions == 3)
										coordSpace =
											new Affine3dCoordinateSpace(
													BigDecimal.valueOf(m_1_1), 
													BigDecimal.valueOf(m_1_2),
													BigDecimal.valueOf(m_1_3),
													BigDecimal.valueOf(m_1_4),
													BigDecimal.valueOf(m_2_1),
													BigDecimal.valueOf(m_2_2),
													BigDecimal.valueOf(m_2_3),
													BigDecimal.valueOf(m_2_4),
													BigDecimal.valueOf(m_3_1),
													BigDecimal.valueOf(m_3_2),
													BigDecimal.valueOf(m_3_3),
													BigDecimal.valueOf(m_3_4));
								}
								else {
		
									scales = new BigDecimal[numLegitDimensions];
									offsets = new BigDecimal[numLegitDimensions];
									axisNames = new String[numLegitDimensions];
		
									int counted = 0;
									for (int i = 0; i < numDimensions; i++) {
										if (i == 0 && xDimension > 1) {
											scales[counted] = BigDecimal.valueOf(xPixelSize);
											offsets[counted] = BigDecimal.valueOf(xOffset);
											axisNames[counted] = "x";
											counted++;
										}
										else if (i == 1 && yDimension > 1) {
											scales[counted] = BigDecimal.valueOf(yPixelSize);
											offsets[counted] = BigDecimal.valueOf(yOffset);
											axisNames[counted] = "y";
											counted++;
										}
										else if (i == 2 && zDimension > 1) {
											scales[counted] = BigDecimal.valueOf(zPixelSize);
											offsets[counted] = BigDecimal.valueOf(zOffset);
											axisNames[counted] = "z";
											counted++;
										}
										else if (i >= 3 && dims.length >= 3 && dims[i] > 1) {
											scales[counted] = BigDecimal.ONE;
											offsets[counted] = BigDecimal.ZERO;
											axisNames[counted] = "unknown";
											counted++;
										}
									}
									coordSpace = new LinearNdCoordinateSpace(scales, offsets);
								}
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
							signedDataFlag = scanMin < 0;
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
							rUnit = xResolution;
							thetaUnit = yResolution;
							zUnit = zResolution;
							if (numZElements > 1) {
								coordSpace = new Cylindrical3dCoordinateSpace(
										BigDecimal.valueOf(rUnit),
										BigDecimal.valueOf(thetaUnit),
										BigDecimal.valueOf(zUnit)
										);
								axisNames = new String[3];
								axisNames[0] = "r";
								axisNames[1] = "theta";
								axisNames[2] = "z";
							}
							else {
								coordSpace = new Polar2dCoordinateSpace(
										BigDecimal.valueOf(rUnit),
										BigDecimal.valueOf(thetaUnit)
										);
								axisNames = new String[2];
								axisNames[0] = "r";
								axisNames[1] = "theta";
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
							signedDataFlag = normMin < 0;
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
							rUnit = 1.0;  // TODO do this better
							thetaUnit = Math.PI * 2 / numAngles;
							zUnit =  1.0;  // TODO do this better
							if (numZElements > 1) {
								coordSpace = new Cylindrical3dCoordinateSpace(
										BigDecimal.valueOf(rUnit),
										BigDecimal.valueOf(thetaUnit),
										BigDecimal.valueOf(zUnit)
										);
								axisNames = new String[3];
								axisNames[0] = "r";
								axisNames[1] = "theta";
								axisNames[2] = "z";
							}
							else {
								coordSpace = new Polar2dCoordinateSpace(
										BigDecimal.valueOf(rUnit),
										BigDecimal.valueOf(thetaUnit)
										);
								axisNames = new String[2];
								axisNames[0] = "r";
								axisNames[1] = "theta";
							}
							break;
						
						default:
							// skip unknown header?
							//c1.goForwardTo(desiredPos);
							System.out.println("ECAT: unknown file type ("+fileType+") : no data was read!");
						}
				
						if (dataType > 0) {
							
							System.out.println("  READING IMAGE DATA FROM POS " + c1.pos);
		
							Allocatable type = value(dataType, signedDataFlag);
							
							frameData = Storage.allocate(type, 1L*dims[0]*dims[1]*numPlanes);
		
							long numElements = frameData.size();
							for (long i = 0; i < numElements; i++) {
								readValue(data, dataType, signedDataFlag, fileIsBigEndian, type);
								frameData.set(i, type);
							}
		
							threeDChunks.add(frameData);
		
							System.out.println("  FINISHED READING PLANE AND POS IS " + c1.pos);
		
							if (c1.pos % 512 > 0) {
								c1.skip(512 - (c1.pos % 512));
							}
						}
						
						dims = numPlanes > 1 ?
								new long[] {dims[0], dims[1], numPlanes}
								:
								new long[] {dims[0], dims[1]};
		
						if (dataType != 0) {
						
							int numChunks = threeDChunks.size();
							for (int chnk = 0; chnk < numChunks; chnk++) {
								
								Allocatable type = value(dataType, signedDataFlag);
								
								DimensionedDataSource<Allocatable> ds =
										DimensionedStorage.allocate(type, dims);
								
								frameData = threeDChunks.get(chnk);
								long numElements = frameData.size();
								for (long u = 0; u < numElements; u++) {
									frameData.get(u, type);
									ds.rawData().set(u, type);
								}
								
								// does the header have a scale factor associated with it? The lowerbound cutoff keeps
								//   us from making a dataset where you can barely distinguish pixels from each other.
								//   a 0.00001 scale of an unsigned 16 bit type fits in range (0, 0.32767)
								
								if (scaleFactor != 0 && scaleFactor != 1 && Math.abs(scaleFactor) > 0.00001) {
									
									System.out.println("SCALING DATA BY SCALEFACTOR "+scaleFactor);
								
									// apply scale factor
									
									if (type instanceof Float64Member) {
										
										// a double data set can be scaled just fine
										
										ScaleByDouble.compute(G.DBL, (double) scaleFactor,
																(IndexedDataSource) ds.rawData(),
																(IndexedDataSource) ds.rawData());
									}
									else if (type instanceof Float32Member) {
		
										// a float data set can be scaled just fine
										
										ScaleByDouble.compute(G.FLT, (double) scaleFactor,
												(IndexedDataSource) ds.rawData(),
												(IndexedDataSource) ds.rawData());
									}
									else {

										// an integer based data set cannot be scaled without some
										//   data loss so transform it into a float data set (because
										//   our scale factor is a float).
		
										Allocatable floatType = G.FLT.construct();
												
										DimensionedDataSource<Float32Member> floatDs =
												DimensionedStorage.allocate(floatType, dims);
										HighPrecRepresentation valAsHP = (HighPrecRepresentation) type;
										HighPrecisionMember hpVal = G.HP.construct();
										HighPrecisionMember scale = G.HP.construct(scaleFactor);
										Float32Member fltVal = G.FLT.construct();
										IndexedDataSource<Allocatable> rawData = ds.rawData();
										long numElems = rawData.size();
										for (long i = 0; i < numElems; i++) {
											rawData.get(i, type);
											valAsHP.toHighPrec(hpVal);
											G.HP.multiply().call(hpVal, scale, hpVal);
											fltVal.fromHighPrec(hpVal);
											floatDs.rawData().set(i, fltVal);
										}
										ds = (DimensionedDataSource) floatDs;
										type = floatType;
									}
								}
								
								ds.setName("Bed " + bedpos + " Gate " + gate + " Frame " + f + " of " + filename);
								ds.setSource(fname);
								
								if (ds.numDimensions() > 0) ds.setAxisType(0, axisNames[0]);
								if (ds.numDimensions() > 1) ds.setAxisType(1, axisNames[1]);
								if (ds.numDimensions() > 2) ds.setAxisType(2, axisNames[2]);
								
								if (coordSpace != null) ds.setCoordinateSpace(coordSpace);
								
								ds.setValueUnit(dataUnits);
								
								merge(images, ds, type);
							}
						}
					}
				}
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
		};
		
		System.out.println("FINAL SUMMARY");
		System.out.println("  FILE BYTE LENGTH = " + file1.length());
		System.out.println("  TOTAL BYTES READ = " + c1.pos);
		
		return images;
	}

	private static Allocatable value(short dataType, boolean signedData) {
		switch (dataType) {
		case 1: // byte
			if (signedData)
				return G.INT8.construct();
			else
				return G.UINT8.construct();
		case 2: // short : VAX_I2 LITTLE
			if (signedData)
				return G.INT16.construct();
			else
				return G.UINT16.construct();
		case 3: // int : VAX_I4 LITTLE
			if (signedData)
				return G.INT32.construct();
			else
				return G.UINT32.construct();
		case 4: // double : VAX_R4 LITTLE
			return G.DBL.construct();
		case 5: // float : IEEE FLT
			return G.FLT.construct();
		case 6: // short : SUN_I2 BIG
			if (signedData)
				return G.INT16.construct();
			else
				return G.UINT16.construct();
		case 7: // int : SUN_I4 big
			if (signedData)
				return G.INT32.construct();
			else
				return G.UINT32.construct();
		default:
			throw new IllegalArgumentException("Unknown data type! "+dataType);
		}
	}

	private static void readValue(DataInputStream d, short dataType, boolean signed, boolean fileIsBigEndian, Allocatable type) throws IOException {
		byte tb;
		short ts;
		int ti;
		float tf;
		double td;

		switch (dataType) {
		case 1: // byte
			tb = readByte(d);
			if (signed)
				((SignedInt8Member) type).setV(tb);
			else
				((UnsignedInt8Member) type).setV(tb);
		case 2: // short : VAX_I2 LITTLE
			ts = readVaxI2(d, fileIsBigEndian);
			if (signed)
				((SignedInt16Member) type).setV(ts);
			else
				((UnsignedInt16Member) type).setV(ts);
			break;
		case 3: // int : VAX_I4 LITTLE
			ti = readVaxI4(d, fileIsBigEndian);
			if (signed)
				((SignedInt32Member) type).setV(ti);
			else
				((UnsignedInt32Member) type).setV(ti);
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
			if (signed)
				((SignedInt16Member) type).setV(ts);
			else
				((UnsignedInt16Member) type).setV(ts);
			break;
		case 7: // int : SUN_I4 big
			ti = readSunI4(d, fileIsBigEndian);
			if (signed)
				((SignedInt32Member) type).setV(ti);
			else
				((UnsignedInt32Member) type).setV(ti);
			break;
		default:
			throw new IllegalArgumentException("Unknown data type! "+dataType);
		}
	}
	
	private static void merge(DataBundle dataSources, DimensionedDataSource<?> dataSource, Allocatable type) {
		
		if (type instanceof UnsignedInt8Member)
			dataSources.mergeUInt8((DimensionedDataSource<UnsignedInt8Member>) dataSource);
		
		else if (type instanceof SignedInt8Member)
			dataSources.mergeInt8((DimensionedDataSource<SignedInt8Member>) dataSource);
		
		else if (type instanceof UnsignedInt16Member)
			dataSources.mergeUInt16((DimensionedDataSource<UnsignedInt16Member>) dataSource);
		
		else if (type instanceof SignedInt16Member)
			dataSources.mergeInt16((DimensionedDataSource<SignedInt16Member>) dataSource);
		
		else if (type instanceof UnsignedInt32Member)
			dataSources.mergeUInt32((DimensionedDataSource<UnsignedInt32Member>) dataSource);
		
		else if (type instanceof SignedInt32Member)
			dataSources.mergeInt32((DimensionedDataSource<SignedInt32Member>) dataSource);
		
		else if (type instanceof Float32Member)
			dataSources.mergeFlt32((DimensionedDataSource<Float32Member>) dataSource);
		
		else if (type instanceof Float64Member)
			dataSources.mergeFlt64((DimensionedDataSource<Float64Member>) dataSource);
		
		// else no known type I can merge
		
		else
			throw new IllegalArgumentException("Unknown data type: "+type.getClass().getName());
	}

	private static byte readByte(DataInputStream str) throws IOException {
		return str.readByte();
	}
	
	private static short readShort(DataInputStream str, boolean fileIsBigEndian) throws IOException {
		short v = str.readShort();
		// TODO
		//if (!fileIsBigEndian) v = swapShort(v);
		return v;
	}
	
	private static int readInt(DataInputStream str, boolean fileIsBigEndian) throws IOException {
		int v = str.readInt();
		if (!fileIsBigEndian) v = swapIntBytes(v);
		return v;
	}
	
	private static float readFloat(DataInputStream str, boolean fileIsBigEndian) throws IOException {
		if (!fileIsBigEndian) {
			int bits = str.readInt();
			bits = swapIntBytes(bits);
			bits = swapIntWords(bits);
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
		if (!fileIsBigEndian) v = swapShort(v);
		return v;
	}

	private static int readVaxI4(DataInputStream str, boolean fileIsBigEndian) throws IOException {
		int v = str.readInt();
		if (!fileIsBigEndian) v = swapIntBytes(v);
		return v;
	}

	// NOTE: to preserve the full accuracy of the vax float I am returning it as a double
	
	private static double readVaxR4(DataInputStream str, boolean fileIsBigEndian) throws IOException {
		int bits = str.readInt();
		if (!fileIsBigEndian) {
			// TODO: also swap bytes within shorts?
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
		if (!fileIsBigEndian) v = swapIntBytes(v);
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
	
	private static int swapIntBytes(int in) {
		int b0 = (in >> 0) & 0xff;
		int b1 = (in >> 8) & 0xff;
		int b2 = (in >> 16) & 0xff;
		int b3 = (in >> 24) & 0xff;
		return (b0 << 24) | (b1 << 16) | (b2 << 8) | (b3 << 0);
	}
	
	private static int swapIntWords(int in) {
		int b0 = (in >> 0) & 0xffff;
		int b1 = (in >> 16) & 0xffff;
		return (b0 << 16) | (b1 << 0);
	}
	
	private static class PositionableInputStream extends InputStream {
		
		private InputStream in;
		private long pos;
		
		public PositionableInputStream(InputStream in) {
			
			this.in = in;
		}

		@Override
		public int read() throws IOException {
			int byt = in.read();
			if (byt >= 0)
				pos++;
			return byt;
		}
		
		@Override
		public int read(byte[] b) throws IOException {

			int numBytesRead = in.read(b);
			if (numBytesRead >= 0)
				pos = pos + numBytesRead;
			return numBytesRead;
		}
		
		@Override
		public int read(byte[] b, int off, int len) throws IOException {

			int numBytesRead = in.read(b, off, len);
			if (numBytesRead >= 0)
				pos = pos + numBytesRead;
			return numBytesRead;
		}
		
		public void goForwardTo(long desiredPos) throws IOException {
			long diff = desiredPos - pos;
			if (diff >= 0) {
				long numSkipped = in.skip(desiredPos - pos);
				pos = pos + numSkipped;
			}
			else
				throw new IllegalArgumentException("not yet supporting backward seeks");
		}
		
		@Override
		public int available() throws IOException {
			return in.available();
		}
		
		@Override
		protected Object clone() throws CloneNotSupportedException {
			throw new CloneNotSupportedException();
		}
		
		@Override
		public void close() throws IOException {
			in.close();
		}
		
		@Override
		public synchronized void mark(int readlimit) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean markSupported() {
			return false;
		}
		@Override
		public synchronized void reset() throws IOException {
			in.reset();
		}
		
		@Override
		public long skip(long n) throws IOException {
			long skipped = in.skip(n);
			pos = pos + skipped;
			return skipped;
		}
		
	}
	
	/*public*/ static void main(String[] args) {
		DataBundle data = Ecat.loadAllDatasets("/home/bdz/images/ecat/099_S_2146_881_2be9_de11.v");
	}
}
