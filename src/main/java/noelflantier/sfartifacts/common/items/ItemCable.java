package noelflantier.sfartifacts.common.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noelflantier.sfartifacts.Ressources;

public class ItemCable extends ItemSFA{

	public ItemCable() {
		super();
		setUnlocalizedName(Ressources.UL_NAME_CABLE);
		setRegistryName(Ressources.UL_NAME_CABLE);
		setHasSubtypes(true);
	}

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }
    
	@Override
    public String getUnlocalizedName(ItemStack itemstack){
    	int i = itemstack.getItemDamage();
    	if (i < 0 || i >= Ressources.UL_NAME_TYPE_CABLE.length){
        	i = 0;
        }
        return super.getUnlocalizedName()+"."+Ressources.UL_NAME_TYPE_CABLE[i];
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs creaT, List list){
    	list.add(new ItemStack(item, 1, 0));
    	list.add(new ItemStack(item, 1, 1));
    	list.add(new ItemStack(item, 1, 2));
    	list.add(new ItemStack(item, 1, 3));    
	}
	
	@Override
    public int getMetadata(int meta){
		return meta;
    }
}
