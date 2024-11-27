package exceptionInfo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;

class BadFileNotFoundException {

  public String input="input";

  public void badFileInputStream1() throws FileNotFoundException {
    // Linux stores a user's home directory path in
    // the environment variable $HOME, Windows in %APPDATA%
    FileInputStream fis = new FileInputStream(System.getenv("APPDATA") + input);
  }

  public void badFileInputStream2() throws IOException {
    try {
      FileInputStream fis = new FileInputStream(System.getenv("APPDATA") + input);
    } catch (FileNotFoundException e) {
      throw new IOException("Unable to retrieve file", e);
    }
  }

  public void badFileInputStream3() throws SecurityIOException {
    try {
      FileInputStream fis = new FileInputStream(System.getenv("APPDATA") + input);
    } catch (FileNotFoundException e) {
      // Log the exception
      throw new SecurityIOException(e);
    }
  }

  public void badFileOutputStream1() throws FileNotFoundException {
    // Linux stores a user's home directory path in
    // the environment variable $HOME, Windows in %APPDATA%
    FileOutputStream fos = new FileOutputStream(System.getenv("APPDATA") + input);
  }

  public void badFileOutputStream2() throws IOException {
    try {
      FileOutputStream fos = new FileOutputStream(System.getenv("APPDATA") + input);
    } catch (FileNotFoundException e) {
      // Log the exception
      throw new IOException("Unable to retrieve file", e);
    }
  }

  public void badFileOutputStream3() throws SecurityIOException {
    try {
      FileOutputStream fos = new FileOutputStream(System.getenv("APPDATA") + input);
    } catch (FileNotFoundException e) {
      // Log the exception
      throw new SecurityIOException(e);
    }
  }

  public void badRandomAccessFile1() throws FileNotFoundException {
    // Linux stores a user's home directory path in
    // the environment variable $HOME, Windows in %APPDATA%
    RandomAccessFile fos = new RandomAccessFile(new File(System.getenv("APPDATA") + input), "r");
  }

  public void badRandomAccessFile2() throws IOException {
    try {
      RandomAccessFile fos = new RandomAccessFile(new File(System.getenv("APPDATA") + input), "r");
    } catch (FileNotFoundException e) {
      // Log the exception
      throw new IOException("Unable to retrieve file", e);
    }
  }

  public void badRandomAccessFile3() throws SecurityIOException {
    try {
      RandomAccessFile fos = new RandomAccessFile(new File(System.getenv("APPDATA") + input), "r");
    } catch (FileNotFoundException e) {
      // Log the exception
      throw new SecurityIOException(e);
    }
  }
}