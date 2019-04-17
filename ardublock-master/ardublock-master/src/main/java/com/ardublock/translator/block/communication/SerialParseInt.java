package com.ardublock.translator.block.communication;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class SerialParseInt extends TranslatorBlock
{
	public SerialParseInt(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		if(!translator.containsSetupCommand("Serial.begin")){
			translator.addSetupCommand("Serial.begin(9600);");
		}
		
		String ret = "Serial.parseInt()";
		
		return codePrefix+ret+codeSuffix;
	}
}
