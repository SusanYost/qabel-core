package de.qabel.core.ui

import de.qabel.core.config.Contact
import de.qabel.core.extensions.CoreTestCase
import de.qabel.core.extensions.createContact
import de.qabel.core.extensions.createIdentity
import org.junit.Test
import org.junit.Assert.*
import org.hamcrest.Matchers.*

class EntityUIExtensionTest() : CoreTestCase {

    val contact : Contact = createContact("Max Moritz Mustermann")

    @Test
    fun testDisplayName(){
        assertThat(contact.displayName(), equalTo(contact.alias))
        contact.nickName = "MaxiMM"
        assertThat(contact.displayName(), equalTo(contact.nickName))
    }

    @Test
    fun testReadableKey(){
        val readableKey = contact.readableKey()
        println(readableKey)
        val lines = readableKey.lines()
        assertThat(lines, hasSize(5))
        lines.forEach {
            if(lines.last() != it){
                assertThat(it.length, equalTo(24))
                val spaces = it.count { it.isWhitespace() }
                assertThat(spaces, equalTo(4))
            }
        }
    }

    @Test
    fun testReadableKeyShort(){
        val readableKey = contact.readableKeyShort()
        println(readableKey)
        val lines = readableKey.lines()
        assertThat(lines, hasSize(2))
        lines.forEach {
            assertThat(it.length, equalTo(24))
            val spaces = it.count { it.isWhitespace() }
            assertThat(spaces, equalTo(4))
        }
    }

    @Test
    fun testInitials() {
        val initials = Contact("Eine kleiner Test", emptyList(), null).initials()
        assertThat(initials.length, equalTo(2))
        assertThat(initials, equalTo("EK"))
        val zwo = Contact("E  ", emptyList(), null).initials()
        assertThat(zwo.length, equalTo(1))
        assertThat(zwo, equalTo("E"))

        val max = contact.initials()
        assertThat(max.length, equalTo(2))
        assertThat(max, equalTo("MM"))

        val identity = createIdentity("Gabba Hobbit")
        assertThat(identity.initials(), equalTo("GH"))
    }

    @Test
    fun testReadableURL(){
        val url = contact.readableUrl()
        assertThat(url.lines(), hasSize(3))
    }

}
