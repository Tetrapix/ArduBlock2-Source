package com.ardublock.translator.block.input;
import com.ardublock.translator.Translator;
import com.ardublock.translator.block.TranslatorBlock;
import com.ardublock.translator.block.exception.SocketNullException;
import com.ardublock.translator.block.exception.SubroutineNotDeclaredException;

public class LGIQtouchSliderGetOffset2Block extends TranslatorBlock
{
	public LGIQtouchSliderGetOffset2Block(Long blockId, Translator translator, String codePrefix,	String codeSuffix, String label)
	{
		super(blockId, translator, codePrefix, codeSuffix, label);
	}


	
	public String toCode() throws SocketNullException, SubroutineNotDeclaredException
	{
		
		 // Header hinzufuegen
		translator.addHeaderFile("LGI_QTouch.h");
		
		
		// Deklarationen hinzufuegen

		String ret = "Slider.getOffset(2)";
		

		return codePrefix + ret + codeSuffix;
	}
	
}