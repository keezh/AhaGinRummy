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
		// 定义SAXParserFactory以便生成SAXParser实例
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();

		// 定义自定义处理对象
		BookHandler handler = new BookHandler(_engine);

		// 开始解析
		parser.parse(in, handler);

	}

	public String serialize(AhaGameEngine engine) throws Exception
	{
		// 定义TransformerFactory以便生成TransformerHandler实例
		SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactory
				.newInstance();
		TransformerHandler handler = factory.newTransformerHandler();

		// 从TransformerHandler得到Transformer对象
		Transformer transformer = handler.getTransformer();

		// 设置输出xml编码
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		// 是否自动对齐补白
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		// 是否忽略xml声明
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

		// 定义writer和result
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);

		handler.setResult(result);

		String uri = "";
		String localName = "";

		char[] ch = null;
		// 开始创建文档

		handler.startDocument();

		// 创建根节点:ahasavior
		handler.startElement(uri, localName, "ahasavior", null);

		// 创建节点playername
		handler.startElement(uri, localName, "playername", null);
		ch = String.valueOf(engine.playerNameString).toCharArray();
		handler.characters(ch, 0, ch.length);
		handler.endElement(uri, localName, "playername");

		// 创建节点soundeffect
		handler.startElement(uri, localName, "soundeffect", null);
		ch = String.valueOf(engine.isSoundEffectOn).toCharArray();
		handler.characters(ch, 0, ch.length);
		handler.endElement(uri, localName, "soundeffect");

		// 创建节点ailevel
		handler.startElement(uri, localName, "ailevel", null);
		ch = String.valueOf(engine.AILevel).toCharArray();
		handler.characters(ch, 0, ch.length);
		handler.endElement(uri, localName, "ailevel");

		// 创建节点currentstage
		handler.startElement(uri, localName, "currentstage", null);
		ch = String.valueOf(engine.currentStage).toCharArray();
		handler.characters(ch, 0, ch.length);
		handler.endElement(uri, localName, "currentstage");

		// 创建节点gamemode
//		handler.startElement(uri, localName, "gamemode", null);
//		ch = String.valueOf(engine.gameMode).toCharArray();
//		handler.characters(ch, 0, ch.length);
//		handler.endElement(uri, localName, "gamemode");

		// 结束根节点
		handler.endElement(uri, localName, "ahasavior");
		// 结束xml文档
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
