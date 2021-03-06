package com.ardublock.translator.block.output;

import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class PinModeOutputBlock extends TranslatorBlock
{
	public PinModeOutputBlock(Long blockId, Translator translator, String codePrefix, String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}

	@Override
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		String number;
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		number = translatorBlock.toCode().replaceAll("\\s*_.new\\b\\s*", "");
		
		String ret = "pinMode("+number.trim()+", OUTPUT);";
		return codePrefix + ret + codeSuffix;
	}

}
