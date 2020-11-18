package com.hiveworkshop.wc3.mdx;

import java.io.IOException;

import de.wc3data.stream.BlizzardDataInputStream;
import de.wc3data.stream.BlizzardDataOutputStream;

public class MdxUtils {

	/*
	 * private static Set<MdxComponent> optionalPool = new HashSet();
	 *
	 * public void addOptionalPool(MdxComponent component){
	 * optionalPool.add(component); }
	 *
	 *
	 * public void executeOptionalPool(){
	 *
	 * }
	 */

	public static MdxModel loadModel(final BlizzardDataInputStream in) throws IOException {
		final MdxModel model = new MdxModel();
		model.load(in);
		return model;
	}

	public static boolean checkOptionalId(final BlizzardDataInputStream in, final String name) throws IOException {

		in.mark(8);

		if (name.equals(in.readCharsAsString(4))) {
			in.reset();
			return true;
		}

		in.reset();
		return false;
	}

	public static String getOptionalId(final BlizzardDataInputStream in) throws IOException {

		in.mark(8);
		try {
			return in.readCharsAsString(4);
		} finally {
			in.reset();
		}
	}

	public static void checkId(final BlizzardDataInputStream in, final String name) throws IOException {
		final String found = in.readCharsAsString(4);
		if (!found.equals(name)) {
			throw new IOException("Error loading model: CheckID failed, required " + name + " found " + found);
		}
	}

	/*
	 * public static boolean checkOptionalId(BlizzardDataInputStream in, String
	 * name) throws IOException { if(lastCheck == null){ lastCheck =
	 * in.readCharsAsString(4); }
	 *
	 * return lastCheck.equals(name); }
	 *
	 * public static void checkId(BlizzardDataInputStream in, String name) throws
	 * IOException {
	 *
	 * if(lastCheck != null){
	 *
	 * if(!name.equals(lastCheck)){ throw new IOException(
	 * "Error loading model: CheckID failed after optinal check, required " + name +
	 * " found " + lastCheck); }
	 *
	 * lastCheck=null;
	 *
	 * }else{
	 *
	 * String found = in.readCharsAsString(4); if (!found.equals(name)) { throw new
	 * IOException("Error loading model: CheckID failed, required " + name +
	 * " found " + found); } } }
	 */

	public static float[] loadFloatArray(final BlizzardDataInputStream in, final int size) throws IOException {
		final float[] array = new float[size];

		for (int i = 0; i < size; i++) {
			array[i] = in.readFloat();
		}
		return array;
	}

	public static int[] loadIntArray(final BlizzardDataInputStream in, final int size) throws IOException {
		if (size == -1) {
			return new int[0];
		}
		final int[] array = new int[size];

		for (int i = 0; i < size; i++) {
			array[i] = in.readInt();
		}

		return array;
	}

	public static short[] loadShortArray(final BlizzardDataInputStream in, final int size) throws IOException {
		final short[] array = new short[size];

		for (int i = 0; i < size; i++) {
			array[i] = (short) (in.readShort() & 0xFFFF);
		}

		return array;
	}

	public static byte[] loadByteArray(final BlizzardDataInputStream in, final int size) throws IOException {
		final byte[] array = new byte[size];

		for (int i = 0; i < size; i++) {
			array[i] = in.readByte();
		}
		return array;
	}

	public static char[] loadCharArray(final BlizzardDataInputStream in, final int size) throws IOException {
		final char[] array = new char[size];

		for (int i = 0; i < size; i++) {
			array[i] = in.readChar();
		}
		return array;
	}

	public static void saveFloatArray(final BlizzardDataOutputStream out, final float[] array) throws IOException {
		for (float v : array) {
			out.writeFloat(v);
		}
	}

	public static void saveIntArray(final BlizzardDataOutputStream out, final int[] array) throws IOException {
		for (int value : array) {
			out.writeInt(value);
		}
	}

	public static void saveShortArray(final BlizzardDataOutputStream out, final short[] array) throws IOException {
		for (short value : array) {
			out.writeNByteInt(value, 2);
		}
	}

	public static void saveByteArray(final BlizzardDataOutputStream out, final byte[] array) throws IOException {
		for (byte b : array) {
			out.writeByte(b);
		}
	}

	public static void saveCharArray(final BlizzardDataOutputStream out, final char[] array) throws IOException {
		out.writeChars(array);
	}
}
