package noelflantier.sfartifacts.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import noelflantier.sfartifacts.Ressources;

public class PacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Ressources.MODID);
	private static int ID = 0;
	
	public static int nextId(){
		return ID++;
	}
	public static void sendToAllAround(IMessage message, TileEntity tile){
    	INSTANCE.sendToAllAround(message, new NetworkRegistry.TargetPoint(tile.getWorld().provider.getDimension(),tile.getPos().getX(),tile.getPos().getY(),tile.getPos().getZ(),64));
	}
	
	public static void sendToAllAroundPlayer(IMessage message, EntityPlayer player){
    	INSTANCE.sendToAllAround(message, new NetworkRegistry.TargetPoint(player.worldObj.provider.getDimension(),player.posX,player.posY,player.posZ,16));
	}
	
	public static void sendToPlayerMP(IMessage message, EntityPlayerMP player){
    	INSTANCE.sendTo(message, player);
	}
	
	public static void sendToAllAround(IMessage message, World world,int x, int y, int z){
    	INSTANCE.sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(),x,y,z,64));
	}
}
