package xyz.jerez.nio.buffer;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @author LQL
 * @since Create in 2021/1/24 21:36
 */
public class CharBufferExamples {

    @Test
    public void test1() {
//        System.out.println((char) 0);
        final ByteBuffer buf = ByteBuffer.wrap("zzm".getBytes());
        System.out.println(buf.toString());
//        char 占2个字节
        final CharBuffer charBuffer = buf.asCharBuffer();
//        1，不足2个字节的会直接丢失
        System.out.println(charBuffer.limit());
//        1
        System.out.println(charBuffer.capacity());
        System.out.println(charBuffer.get());
        System.out.println(charBuffer.toString());
    }

    /**
     * ByteBuffer 以指定编码转 CharBuffer
     */
    @Test
    public void test2() throws CharacterCodingException {
        final ByteBuffer buf = ByteBuffer.wrap("zzm我".getBytes());
//        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
//        final CharBuffer charBuffer = decoder.decode(buf);
//        可直接decode
        final CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buf);
        System.out.println(charBuffer.position());
        System.out.println(charBuffer.limit());
//        decode中最后flip了。所以不需要我们再flip
//        System.out.println(charBuffer.get());
        System.out.println(charBuffer.toString());
    }
}
