package noelflantier.sfartifacts.common.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import noelflantier.sfartifacts.common.tileentities.pillar.TileMasterPillar;

public class PacketUnsetPillar  implements IMessage, IMessageHandler<PacketUnsetPillar, IMessage> {
	
	public int x;
	public int y;
	public int z;
	
	public PacketUnsetPillar(){
		
	}
	
	public PacketUnsetPillar(TileMasterPillar t){
		this.x = t.getPos().getX();
		this.y = t.getPos().getY();
		this.z = t.getPos().getZ();
	}
	
	@Override
	public IMessage onMessage(PacketUnsetPillar message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(new Runnable(){
			@Override
			public void run() {
				TileEntity te = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(new BlockPos(message.x,message.y, message.z));
				if(te!=null && te instanceof TileMasterPillar) {
					((TileMasterPillar)te).master = null;
				}
			}}
		);
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {	
	    x = buf.readInt();
	    y = buf.readInt();
	    z = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {	
	    buf.writeInt(x);
	    buf.writeInt(y);
	    buf.writeInt(z);
	}

}
