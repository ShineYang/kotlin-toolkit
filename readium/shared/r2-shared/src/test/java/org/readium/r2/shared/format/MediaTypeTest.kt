/*
 * Module: r2-shared-kotlin
 * Developers: Mickaël Menu
 *
 * Copyright (c) 2020. Readium Foundation. All rights reserved.
 * Use of this source code is governed by a BSD-style license which is detailed in the
 * LICENSE file present in the project repository where this source code is maintained.
 */

package org.readium.r2.shared.format

import org.junit.Assert.*
import org.junit.Test

class MediaTypeTest {

    @Test
    fun `returns null for invalid types`() {
        assertNull(MediaType.parse("application"))
        assertNull(MediaType.parse("application/atom+xml/extra"))
    }

    @Test
    fun `to string`() {
        assertEquals(
            "application/atom+xml;profile=opds-catalog",
            MediaType.parse("application/atom+xml;profile=opds-catalog")?.toString()
        )
    }

    @Test
    fun `to string is normalized`() {
        assertEquals(
            "application/atom+xml;a=0;profile=OPDS-CATALOG",
            MediaType.parse("APPLICATION/ATOM+XML;PROFILE=OPDS-CATALOG   ;   a=0")?.toString()
        )
        // Parameters are sorted by name
        assertEquals(
            "application/atom+xml;a=0;b=1",
            MediaType.parse("application/atom+xml;a=0;b=1")?.toString()
        )
        assertEquals(
            "application/atom+xml;a=0;b=1",
            MediaType.parse("application/atom+xml;b=1;a=0")?.toString()
        )
    }

    @Test
    fun `get type`() {
        assertEquals(
            "application",
            MediaType.parse("application/atom+xml;profile=opds-catalog")?.type
        )
        assertEquals("*", MediaType.parse("*/jpeg")?.type)
    }

    @Test
    fun `get subtype`() {
        assertEquals(
            "atom+xml",
            MediaType.parse("application/atom+xml;profile=opds-catalog")?.subtype
        )
        assertEquals("*", MediaType.parse("image/*")?.subtype)
    }

    @Test
    fun `get parameters`() {
        assertEquals(
            mapOf(
                "type" to "entry",
                "profile" to "opds-catalog"
            ),
            MediaType.parse("application/atom+xml;type=entry;profile=opds-catalog")?.parameters
        )
    }

    @Test
    fun `get empty parameters`() {
        assertTrue(MediaType.parse("application/atom+xml")!!.parameters.isEmpty())
    }

    @Test
    fun `get parameters with whitespaces`() {
        assertEquals(
            mapOf(
                "type" to "entry",
                "profile" to "opds-catalog"
            ),
            MediaType.parse("application/atom+xml    ;    type=entry   ;    profile=opds-catalog   ")?.parameters
        )
    }

    @Test
    fun `get structured syntax suffix`() {
        assertNull(MediaType.parse("foo/bar")?.structuredSyntaxSuffix)
        assertNull(MediaType.parse("application/zip")?.structuredSyntaxSuffix)
        assertEquals("+zip", MediaType.parse("application/epub+zip")?.structuredSyntaxSuffix)
        assertEquals("+zip", MediaType.parse("foo/bar+json+zip")?.structuredSyntaxSuffix)
    }

    @Test
    fun `get charset`() {
        assertNull(MediaType.parse("text/html")?.charset)
        assertEquals(Charsets.UTF_8, MediaType.parse("text/html;charset=utf-8")?.charset)
        assertEquals(Charsets.UTF_16, MediaType.parse("text/html;charset=utf-16")?.charset)
    }

    @Test
    fun `type, subtype and parameter names are lowercased`() {
        val mediaType = MediaType.parse("APPLICATION/ATOM+XML;PROFILE=OPDS-CATALOG")
        assertEquals("application", mediaType?.type)
        assertEquals("atom+xml", mediaType?.subtype)
        assertEquals(mapOf("profile" to "OPDS-CATALOG"), mediaType?.parameters)
    }

    @Test
    fun `charset value is uppercased`() {
        assertEquals("UTF-8", MediaType.parse("text/html;charset=utf-8")?.parameters?.get("charset"))
    }

    @Test
    fun equality() {
        assertEquals(MediaType.parse("application/atom+xml")!!, MediaType.parse("application/atom+xml")!!)
        assertEquals(MediaType.parse("application/atom+xml;profile=opds-catalog")!!, MediaType.parse("application/atom+xml;profile=opds-catalog")!!)
        assertNotEquals(MediaType.parse("application/atom+xml")!!, MediaType.parse("application/atom")!!)
        assertNotEquals(MediaType.parse("application/atom+xml")!!, MediaType.parse("text/atom+xml")!!)
        assertNotEquals(MediaType.parse("application/atom+xml;profile=opds-catalog")!!, MediaType.parse("application/atom+xml")!!)
    }

    @Test
    fun `equality ignores case of type, subtype and parameter names`() {
        assertEquals(
            MediaType.parse("application/atom+xml;profile=opds-catalog")!!,
            MediaType.parse("APPLICATION/ATOM+XML;PROFILE=opds-catalog")!!
        )
        assertNotEquals(
            MediaType.parse("application/atom+xml;profile=opds-catalog")!!,
            MediaType.parse("APPLICATION/ATOM+XML;PROFILE=OPDS-CATALOG")!!
        )
    }

    @Test
    fun `equality ignores parameters order`() {
        assertEquals(
            MediaType.parse("application/atom+xml;type=entry;profile=opds-catalog")!!,
            MediaType.parse("application/atom+xml;profile=opds-catalog;type=entry")!!
        )
    }

    @Test
    fun `equality ignores charset case`() {
        assertEquals(
            MediaType.parse("application/atom+xml;charset=utf-8")!!,
            MediaType.parse("application/atom+xml;charset=UTF-8")!!
        )
    }

    @Test
    fun `contains equal media type`() {
        assertTrue(MediaType.parse("text/html;charset=utf-8")
            !!.contains(MediaType.parse("text/html;charset=utf-8")))
    }

    @Test
    fun `contains must match parameters`() {
        assertFalse(MediaType.parse("text/html;charset=utf-8")
            !!.contains(MediaType.parse("text/html;charset=ascii")))
        assertFalse(MediaType.parse("text/html;charset=utf-8")
            !!.contains(MediaType.parse("text/html")))
    }

    @Test
    fun `contains ignores parameters order`() {
        assertTrue(MediaType.parse("text/html;charset=utf-8;type=entry")
            !!.contains(MediaType.parse("text/html;type=entry;charset=utf-8")))
    }

    @Test
    fun `contains ignore extra parameters`() {
        assertTrue(MediaType.parse("text/html")
            !!.contains(MediaType.parse("text/html;charset=utf-8")))
    }

    @Test
    fun `contains supports wildcards`() {
        assertTrue(MediaType.parse("*/*")
            !!.contains(MediaType.parse("text/html;charset=utf-8")))
        assertTrue(MediaType.parse("text/*")
            !!.contains(MediaType.parse("text/html;charset=utf-8")))
        assertFalse(MediaType.parse("text/*")
            !!.contains(MediaType.parse("application/zip")))
    }

    @Test
    fun `contains from string`() {
        assertTrue(MediaType.parse("text/html;charset=utf-8")
            !!.contains("text/html;charset=utf-8"))
    }

    @Test
    fun `is part of equal media type`() {
        assertTrue(MediaType.parse("text/html;charset=utf-8")
            !!.isPartOf(MediaType.parse("text/html;charset=utf-8")))
    }

    @Test
    fun `is part of must match parameters`() {
        assertFalse(MediaType.parse("text/html;charset=ascii")
            !!.isPartOf(MediaType.parse("text/html;charset=utf-8")))
        assertFalse(MediaType.parse("text/html")
            !!.isPartOf(MediaType.parse("text/html;charset=utf-8")))
    }

    @Test
    fun `is part of ignores parameters order`() {
        assertTrue(MediaType.parse("text/html;charset=utf-8;type=entry")
            !!.isPartOf(MediaType.parse("text/html;type=entry;charset=utf-8")))
    }

    @Test
    fun `is part of ignores extra parameters`() {
        assertTrue(MediaType.parse("text/html;charset=utf-8")
            !!.isPartOf(MediaType.parse("text/html")))
    }

    @Test
    fun `is part of supports wildcards`() {
        assertTrue(MediaType.parse("text/html;charset=utf-8")
            !!.isPartOf(MediaType.parse("*/*")))
        assertTrue(MediaType.parse("text/html;charset=utf-8")
            !!.isPartOf(MediaType.parse("text/*")))
        assertFalse(MediaType.parse("application/zip")
            !!.isPartOf(MediaType.parse("text/*")))
    }

    @Test
    fun `is part of from string`() {
        assertTrue(MediaType.parse("text/html;charset=utf-8")
            !!.isPartOf("text/html;charset=utf-8"))
    }

    @Test
    fun `is ZIP`() {
        assertFalse(MediaType.parse("text/plain")!!.isZip)
        assertTrue(MediaType.parse("application/zip")!!.isZip)
        assertTrue(MediaType.parse("application/zip;charset=utf-8")!!.isZip)
        assertTrue(MediaType.parse("application/epub+zip")!!.isZip)
        // These media types must be explicitly matched since they don't have any ZIP hint
        assertTrue(MediaType.parse("application/audiobook+lcp")!!.isZip)
        assertTrue(MediaType.parse("application/pdf+lcp")!!.isZip)
    }

    @Test
    fun `is JSON`() {
        assertFalse(MediaType.parse("text/plain")!!.isJson)
        assertTrue(MediaType.parse("application/json")!!.isJson)
        assertTrue(MediaType.parse("application/json;charset=utf-8")!!.isJson)
        assertTrue(MediaType.parse("application/opds+json")!!.isJson)
    }

    @Test
    fun `is OPDS`() {
        assertFalse(MediaType.parse("text/html")!!.isOpds)
        assertTrue(MediaType.parse("application/atom+xml;profile=opds-catalog")!!.isOpds)
        assertTrue(MediaType.parse("application/atom+xml;type=entry;profile=opds-catalog")!!.isOpds)
        assertTrue(MediaType.parse("application/opds+json")!!.isOpds)
        assertTrue(MediaType.parse("application/opds-publication+json")!!.isOpds)
        assertTrue(MediaType.parse("application/opds+json;charset=utf-8")!!.isOpds)
    }

    @Test
    fun `is HTML`() {
        assertFalse(MediaType.parse("application/opds+json")!!.isHtml)
        assertTrue(MediaType.parse("text/html")!!.isHtml)
        assertTrue(MediaType.parse("application/xhtml+xml")!!.isHtml)
        assertTrue(MediaType.parse("text/html;charset=utf-8")!!.isHtml)
    }

    @Test
    fun `is bitmap`() {
        assertFalse(MediaType.parse("text/html")!!.isBitmap)
        assertTrue(MediaType.parse("image/bmp")!!.isBitmap)
        assertTrue(MediaType.parse("image/gif")!!.isBitmap)
        assertTrue(MediaType.parse("image/jpeg")!!.isBitmap)
        assertTrue(MediaType.parse("image/png")!!.isBitmap)
        assertTrue(MediaType.parse("image/tiff")!!.isBitmap)
        assertTrue(MediaType.parse("image/tiff")!!.isBitmap)
        assertTrue(MediaType.parse("image/tiff;charset=utf-8")!!.isBitmap)
    }

    @Test
    fun `is RWPM`() {
        assertFalse(MediaType.parse("text/html")!!.isRwpm)
        assertTrue(MediaType.parse("application/audiobook+json")!!.isRwpm)
        assertTrue(MediaType.parse("application/divina+json")!!.isRwpm)
        assertTrue(MediaType.parse("application/webpub+json")!!.isRwpm)
        assertTrue(MediaType.parse("application/webpub+json;charset=utf-8")!!.isRwpm)
    }

}
