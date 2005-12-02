// Copyright 2001-2005, FreeHEP.
package org.freehep.graphics2d.font.encoding;

import java.io.*;
import java.util.StringTokenizer;

/**
 * CharTableConverter class generates java files from
 * given unicode formatted txt file to use with Lookup class.
 * Usage java CharTableConverter encoding.unicode.txt [encoding type]
 *
 * @author Sami Kama
 * @version $Id: src/main/java/org/freehep/graphics2d/font/encoding/CharTableConverter.java f21547d41a73 2005/12/02 00:39:35 duns $
 */
public class CharTableConverter {
    private BufferedReader textFile = null;
	private PrintWriter javaOut = null;
	private int encType=1; 

	public void putHeaders(String pkg,  String className) throws Exception {
		javaOut.println("//Generated by CharTableConverter");
		javaOut.println("//!!DO NOT EDIT");
		javaOut.println("package "+pkg+";"); 
		javaOut.println();
		javaOut.println("import java.util.*;");
		javaOut.println();
		javaOut.println("/**");
		javaOut.println(" * Generated "+className+" Encoding Table.");
		javaOut.println(" *");
		javaOut.println(" * @author org.freehep.graphics2d.font.CharTableConverter");
		javaOut.println(" * @version $Id: src/main/java/org/freehep/graphics2d/font/encoding/CharTableConverter.java f21547d41a73 2005/12/02 00:39:35 duns $");		
		javaOut.println(" */");
		javaOut.println("public class "+className+" extends AbstractCharTable {");
		javaOut.println("\tprivate Hashtable unicodeToName = new Hashtable();");
		javaOut.println("\tprivate Hashtable nameToUnicode = new Hashtable();");
		javaOut.println("\tprivate Hashtable nameToEnc = new Hashtable();");
		javaOut.println("\tprivate String[] encToName = new String[256];");
		javaOut.println("\tpublic "+className+"() {");
	}

	public void putMethods(String encoding, String tableName) throws Exception {

	        /*
	        javaOut.println("\tpublic String toName(char c){");
		javaOut.println("\t\treturn((String)(unicodeToName.get(new Character(c))));");
		javaOut.println("\t}");
		javaOut.println();
                */
		javaOut.println("\tpublic String toName(Character c){");
		javaOut.println("\t\treturn((String)unicodeToName.get(c));");
		javaOut.println("\t}");
		javaOut.println();
		javaOut.println("\tpublic String toName(int enc){");
		javaOut.println("\t\tif(enc!=0)");
		javaOut.println("\t\treturn(encToName[enc]);");
		javaOut.println("\t\treturn(null);");
		javaOut.println("\t}");
		javaOut.println();
		/*
		javaOut.println("\tpublic String toName(Integer enc){");
		javaOut.println("\t\treturn(encToName[enc.intValue()]);");
		javaOut.println("\t}");
		javaOut.println();
		*/
		javaOut.println("\tpublic int toEncoding(String name){");
		javaOut.println("\t\treturn(((Integer)(nameToEnc.get(name))).intValue());");
		javaOut.println("\t}");
		javaOut.println();
		javaOut.println("\tpublic char toUnicode(String name){");
		javaOut.println("\t\treturn(((Character)(nameToUnicode.get(name))).charValue());");
		javaOut.println("\t}");
		javaOut.println();
		javaOut.println("\tpublic String getName(){");
		javaOut.println("\treturn(\""+tableName+"\");");
		javaOut.println("\t}");
		javaOut.println();
		javaOut.println("\tpublic String getEncoding(){");
		javaOut.println("\t	return(\""+encoding+"\");");
		javaOut.println("\t}");
		javaOut.println();
		javaOut.println("}");
		javaOut.flush();
		javaOut.close();
	}

	public void openFiles(File in, String outName) throws Exception{
		InputStream textIn = new FileInputStream(in);
		Reader textFileReader = new InputStreamReader(textIn,"UTF-16");

        File out = new File(outName);
        out.getParentFile().mkdirs();
		Writer javaFileWriter = new FileWriter(out);
		textFile = new BufferedReader(textFileReader);
		javaOut = new PrintWriter(javaFileWriter);
	}

	public void convertTxtToHash(String encoding) throws Exception{
		String buff = new String();
		String charCode = new String();
		String charName = new String();
		String enc = new String();
		String hexformat = new String();

		if (encoding.equals("ISO")) encType=5;
		if (encoding.equals("PDF")) encType=4;
		if (encoding.equals("WIN")) encType=3;
		if (encoding.equals("MAC")) encType=2;
		if (encoding.equals("STD")) encType=1;


		while((buff=textFile.readLine())!=null){

			StringTokenizer st = new StringTokenizer(buff,"\u0009\u0020");

			if (!st.hasMoreTokens()) continue;
			charCode=st.nextToken();
			if (charCode.equals("##")) continue;

			if (!st.hasMoreTokens()) continue;
			charName=st.nextToken();

			for (int i=0;i<encType;i++){
				if (!st.hasMoreTokens()) continue;
				enc = st.nextToken();
			}

				if (charCode.startsWith("\\u")){
				int cCL = charCode.length();
				javaOut.println();
				hexformat = charCode.substring(cCL-4,cCL);
				javaOut.print("\t\t\t");
				javaOut.println("unicodeToName.put(new Character((char)0x"+
					hexformat+"),\""+charName+"\");");

				javaOut.print("\t\t\t");
				javaOut.println("nameToUnicode.put(\""
					+charName+"\""+", new Character((char)0x"+hexformat+"));");

			} else {
				hexformat = "00"+(Integer.toHexString((int)charCode.charAt(0)));
				javaOut.println();
				javaOut.print("\t\t\t");
				javaOut.println("unicodeToName.put(new Character((char)0x"+
					hexformat.substring(hexformat.length()-4,hexformat.length())+
					"),\""+charName+"\");");

				javaOut.print("\t\t\t");
				javaOut.println("nameToUnicode.put(\""+
						charName+"\""+",new Character((char)0x"+
					hexformat.substring(hexformat.length()-4,hexformat.length())+
						"));");

				
			}

			Integer encodingInt = Integer.decode(enc);
			if (encodingInt.intValue() >= 0) {
				    
			    javaOut.print("\t\t\t");
			    javaOut.println("nameToEnc.put(\""+charName+
					    "\", new Integer("+encodingInt+"));");
			    
			    javaOut.print("\t\t\t");
			    javaOut.println("encToName["+encodingInt+"]=\""+charName+"\";");
			}
			
		}


 		javaOut.println();
		javaOut.println("\t\t}");
		javaOut.println();

	}


	public static void main(String args[]) throws Exception {

		if ((args.length<3)||(args.length>4)){
			System.err.println("Usage: CharTableConverter destdir package txtfile [Encoding type]");
			System.exit(0);
		}
		CharTableConverter converter = new CharTableConverter();
		
		File src = new File(args[2]);
		
		String encoding = (args.length==4) ? args[3] : "";
		
		String tableName = src.getName();
		int dot = tableName.indexOf(".");
		if (dot >= 0) tableName = tableName.substring(0,dot);
		
		String className = encoding+tableName;
		
		String pkg = args[1];
		
		String destFile = args[0]+File.separator+pkg.replace('.', File.separatorChar)+File.separator+className+".java";
		    
		converter.openFiles(src, destFile);
		converter.putHeaders(pkg, className);
		converter.convertTxtToHash(encoding);
        converter.putMethods(encoding, tableName);
	}

}
