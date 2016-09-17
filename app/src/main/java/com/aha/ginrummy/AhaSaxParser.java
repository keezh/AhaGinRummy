package com.aha.ginrummy;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class AhaSaxParser
{
	public void parser(InputStream in, AhaGameEngine _engine) throws Exception
	{
		// ����SAXParserFactory�Ա�����SAXParserʵ��
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		// �����Զ��崦�����
		BookHandler handler = new BookHandler(_engine);

		// ��ʼ����
		parser.parse(in, handler);

	}

	public String serialize(AhaGameEngine engine) throws Exception
	{
		// ����TransformerFactory�Ա�����TransformerHandlerʵ��
		SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactory
				.newInstance();
		TransformerHandler handler = factory.newTransformerHandler();

		// ��TransformerHandler�õ�Transformer����
		Transformer transformer = handler.getTransformer();

		// �������xml����
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		// �Ƿ��Զ����벹��
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		// �Ƿ����xml����
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

		// ����writer��result
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);

		handler.setResult(result);

		String uri = "";
		String localName = "";

		char[] ch = null;
		// ��ʼ�����ĵ�

		handler.startDocument();

		// �������ڵ�:ahasavior
		handler.startElement(uri, localName, "ahasavior", null);

		// �����ڵ�playername
		handler.startElement(uri, localName, "playername", null);
		ch = String.valueOf(engine.playerNameString).toCharArray();
		handler.characters(ch, 0, ch.length);
		handler.endElement(uri, localName, "playername");

		// �����ڵ�soundeffect
		handler.startElement(uri, localName, "soundeffect", null);
		ch = String.valueOf(engine.isSoundEffectOn).toCharArray();
		handler.characters(ch, 0, ch.length);
		handler.endElement(uri, localName, "soundeffect");

		// �����ڵ�ailevel
		handler.startElement(uri, localName, "ailevel", null);
		ch = String.valueOf(engine.AILevel).toCharArray();
		handler.characters(ch, 0, ch.length);
		handler.endElement(uri, localName, "ailevel");

		// �����ڵ�currentstage
		handler.startElement(uri, localName, "currentstage", null);
		ch = String.valueOf(engine.currentStage).toCharArray();
		handler.characters(ch, 0, ch.length);
		handler.endElement(uri, localName, "currentstage");

		// �����ڵ�gamemode
//		handler.startElement(uri, localName, "gamemode", null);
//		ch = String.valueOf(engine.gameMode).toCharArray();
//		handler.characters(ch, 0, ch.length);
//		handler.endElement(uri, localName, "gamemode");

		// �������ڵ�
		handler.endElement(uri, localName, "ahasavior");
		// ����xml�ĵ�
		handler.endDocument();

		return writer.toString();
	}

	class BookHandler extends DefaultHandler
	{
		private AhaGameEngine engine;
		private StringBuilder builder;

		public BookHandler(AhaGameEngine _engine)
		{
			engine = _engine;
		}

		@Override
		public void startDocument() throws SAXException
		{
			super.startDocument();
			builder = new StringBuilder();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException
		{
			super.startElement(uri, localName, qName, attributes);

			builder.setLength(0);
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException
		{
			super.characters(ch, start, length);
			builder.append(ch, start, length);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException
		{
			super.endElement(uri, localName, qName);
			if (localName.equals("playername"))
			{
				engine.playerNameString = builder.toString();
			} else if (localName.equals("soundeffect"))
			{
				engine.isSoundEffectOn = Boolean.parseBoolean(builder
						.toString());
			} else if (localName.equals("ailevel"))
			{
				engine.AILevel = (Integer.parseInt(builder.toString()));
			} else if (localName.equals("currentstage"))
			{
				engine.currentStage = (Integer.parseInt(builder.toString()));
			} 
//			else if (localName.equals("gamemode"))
//			{
//				engine.gameMode = (Integer.parseInt(builder.toString()));
//			}
		}
	}
}
