package self.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.TimeUnit;

/**
 * @author linsong.chen
 * @date 2020-05-29 10:31
 */
public class ClientHandler extends SimpleChannelInboundHandler {

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext){
        channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("Netty Rocks!", CharsetUtil.UTF_8));
    }


//    public void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf in)  throws Exception{
//        System.out.println("Client received: " + in.toString(CharsetUtil.UTF_8));
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause){
        cause.printStackTrace();
        channelHandlerContext.close();
    }


    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        ByteBuf in = (ByteBuf) o;
        System.out.println("Client received: " + in.toString(CharsetUtil.UTF_8));
        final ChannelPromise promise = channelHandlerContext.newPromise();
        promise.addListener(new GenericFutureListener() {
            public void operationComplete(Future future) throws Exception {
                if (future.isSuccess()){
                    System.out.println("promise operationComplete");
                }
                if (future.cause() != null){
                    future.cause().printStackTrace();
                    System.out.println("promise cause");
                }
                if (future.isCancellable()){
                    System.out.println("promise cancel");
                }
            }
        });


        channelHandlerContext.channel().eventLoop().execute(new Runnable() {
            public void run() {
                System.out.println("下一次事件循环,根据执行成功与否，回调promise");
                promise.cancel(false);

//                promise.setFailure(new NullPointerException());
            }
        });

        System.out.println("heelp1");
        promise.awaitUninterruptibly(1, TimeUnit.SECONDS);
        System.out.println("heelp2");
    }
}
