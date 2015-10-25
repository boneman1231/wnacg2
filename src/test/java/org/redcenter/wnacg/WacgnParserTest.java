package org.redcenter.wnacg;

import static org.junit.Assert.*;

import org.junit.Test;

public class WacgnParserTest {

	private Parser parser = new WacgnParser();
	private String mainUrl = "http://www.wnacg.com/photos-index-aid-20178.html";
	private String indexUrl = "http://www.wnacg.com/photos-index-page-3-aid-20178.html";
	private String imageUrl = "http://www.wnacg.com/photos-view-id-1392210.html";

	@Test
	public void testIsComicMainPage() {
		assertTrue(parser.isComicMainPage(mainUrl));
		assertFalse(parser.isComicIndexPage(mainUrl));
		assertFalse(parser.isComicImagePage(mainUrl));
	}

	@Test
	public void testIsComicIndexPage() {
		assertTrue(parser.isComicIndexPage(indexUrl));
		assertFalse(parser.isComicMainPage(indexUrl));
		assertFalse(parser.isComicImagePage(indexUrl));
	}

	@Test
	public void testIsComicImagePage() {
		assertTrue(parser.isComicImagePage(imageUrl));
		assertFalse(parser.isComicMainPage(imageUrl));
		assertFalse(parser.isComicIndexPage(imageUrl));
	}

	@Test
	public void testGetComicMainPageFromIndexPage() {
		assertEquals(mainUrl, parser.getComicMainPageFromIndexPage(indexUrl));
	}

	@Test
	public void testGetComicMainPageFromImagePage() {
		assertEquals(mainUrl, parser.getComicMainPageFromImagePage(imageUrl));
	}

	@Test
	public void testGetComiceImages() {
		String url = "http://www.wnacg.com/photos-index-aid-20177.html";
		String name = parser.getComicNameFromMainPage(url);
		System.out.println("Comic name: " + name);
		assertEquals("[しのぎ鋭介]委員長の恋人", name);
	}

	@Test
	public void testGetImages() {

	}

}
