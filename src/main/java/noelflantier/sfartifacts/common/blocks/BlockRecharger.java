package noelflantier.sfartifacts.common.blocks;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noelflantier.sfartifacts.Ressources;
import noelflantier.sfartifacts.SFArtifacts;
import noelflantier.sfartifacts.common.handlers.ModGUIs;
import noelflantier.sfartifacts.common.tileentities.TileRecharger;

public class BlockRecharger extends ABlockSFAContainer{

    protected static final AxisAlignedBB R_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1D, 0.7D, 1D);
    
	public BlockRecharger(Material materialIn) {
		super(materialIn);
		setRegistryName(Ressources.UL_NAME_RECHARGER);
        setUnlocalizedName(Ressources.UL_NAME_RECHARGER);
		this.setHarvestLevel("pickaxe",1);
		this.setHardness(2.0F);
		this.setResistance(10000.0F);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileRecharger();
	}
	
    @Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return R_AABB;
    }

	@Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side){
		if(side==EnumFacing.UP)
			return false;
		return true;
    }
	
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ){
        if(worldIn.isRemote)
    		return true;
        
		playerIn.openGui(SFArtifacts.instance, ModGUIs.guiIDRecharger, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
    
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state){
        return EnumBlockRenderType.MODEL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
	public boolean dropWithNBT(IBlockState state){
		return true;
	}
    
    @Override
    public boolean isOpaqueCube(IBlockState state){
        return false;
    }
    
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		if(stack.getTagCompound()==null)
			return;
		if(stack.getTagCompound().getTag("BlockEntityTag") == null)
			return;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)){
			NBTTagCompound t = (NBTTagCompound) stack.getTagCompound().getTag("BlockEntityTag");
			list.add("Energy : "+t.getInteger("Energy")+" RF");
		}else{
			list.add(TextFormatting.WHITE + "" + TextFormatting.ITALIC +"<Hold Shift>");
		}
	}

}
