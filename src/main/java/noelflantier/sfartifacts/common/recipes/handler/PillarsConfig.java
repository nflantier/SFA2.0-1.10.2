package noelflantier.sfartifacts.common.recipes.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import noelflantier.sfartifacts.common.blocks.BlockMaterials;
import noelflantier.sfartifacts.common.blocks.SFAProperties.EnumPillarBlockType;
import noelflantier.sfartifacts.common.helpers.Utils;
import noelflantier.sfartifacts.common.tileentities.pillar.TileBlockPillar;
import noelflantier.sfartifacts.common.tileentities.pillar.TileInterfacePillar;
import noelflantier.sfartifacts.common.tileentities.pillar.TileMasterPillar;
import noelflantier.sfartifacts.common.tileentities.pillar.TileRenderPillarModel;

public class PillarsConfig {
	static final PillarsConfig instance = new PillarsConfig();
	private static final String CORE_FILE_NAME = "pillarsConfig.json";

	private static final String KEY_PILLARS = "pillars";
	private static final String KEY_ENERGY_CAPACITY = "energycapacity";
	private static final String KEY_FLUID_CAPACITY = "fluidcapacity";
	private static final String KEY_NATURAL_RATIO = "naturalratio";
	private static final String KEY_STRUCTURE = "structure";
	
	
	private static final String P_NORMAL_BLOCK = "block";
	private static final String P_INTERFACE = "interface";
	private static final String P_BLOCK_ACTIVATE = "blockactivate";
	private static final String P_MASTER = "master";
	
	public static PillarsConfig getInstance() {
		return instance;
	}
	
	public Map<String, Pillar> nameToPillar = new HashMap<String, Pillar>();
	public List<String> nameOrderedBySize;

	private PillarsConfig(){
		String configText;
		JsonElement root;
		JsonObject rootObj;
		JsonObject pillarsObj;

		try {
			configText = Utils.getFileFromConfig(CORE_FILE_NAME, false);
			root = new JsonParser().parse(configText);
			rootObj = root.getAsJsonObject();
			pillarsObj = rootObj.getAsJsonObject(KEY_PILLARS);

			for (Entry<String, JsonElement> entry : pillarsObj.entrySet()) {
				String name = entry.getKey();
				if(name==null || name.equals("")){
					System.out.println("Pillar name cant be null");
					continue;
				}
				if(nameToPillar.containsKey(name)){
					System.out.println("Pillar "+name+" allready registered");
					continue;
				}
				processPillar(name, entry.getValue().getAsJsonObject());
			}
			nameOrderedBySize = new ArrayList<String>(nameToPillar.size());
			for(Entry<String, Pillar> entry : nameToPillar.entrySet()){
				orderMap(entry.getValue());
			}
			//for(String s : nameOrderedBySize){
			//	System.out.println("........................ "+s);
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void orderMap( Pillar el){
		int idx = 0;
		if(nameOrderedBySize.size()<=0){
			nameOrderedBySize.add(el.name);
			return;
		}
		for(String s : nameOrderedBySize){
			if(el.mapStructure.size()<nameToPillar.get(s).mapStructure.size())
				idx+=1;
		}
		nameOrderedBySize.add(idx, el.name);
	}
	
	private void processPillar(String name, JsonObject pi) {
		boolean flag = false;
		Pillar p = new Pillar(name);
		if(pi.has(KEY_ENERGY_CAPACITY))
			p.energyCapacity = pi.get(KEY_ENERGY_CAPACITY).getAsInt();
		else
			flag = true;
		if(pi.has(KEY_FLUID_CAPACITY))
			p.fluidCapacity = pi.get(KEY_FLUID_CAPACITY).getAsInt();
		else
			flag = true;
		if(pi.has(KEY_NATURAL_RATIO))
			p.naturalRatio = pi.get(KEY_NATURAL_RATIO).getAsFloat();
		else
			flag = true;
		if(!flag){
			boolean havemaster = false;
			boolean haveblocktoactivate = false;
			List<BlockPos> ms = new ArrayList<BlockPos>();
			Map<String,String> inte = new HashMap<String,String>();
			for (Entry<String, JsonElement> entry : pi.get(KEY_STRUCTURE).getAsJsonObject().entrySet()) {
				int[] c = getXYZ(entry.getKey(), "_");
				BlockPos co = new BlockPos(c[0],c[1],c[2]);
				ms.add(co);
				if(P_BLOCK_ACTIVATE.equals(entry.getValue().getAsString())){
					p.blockToActivate = co;
					haveblocktoactivate = true;
				}else if(P_MASTER.equals(entry.getValue().getAsString())){
					p.blockMaster = co;
					havemaster = true;
				}else if(P_NORMAL_BLOCK.equals(entry.getValue().getAsString())){
					
				}else if(entry.getValue().getAsString().startsWith(P_INTERFACE)){
					String[] separated = entry.getValue().getAsString().split("[:]");
					if(separated!=null && separated.length>1)
						inte.put(entry.getKey(), separated[1]);
				}
			}
			
			if(!havemaster){
				System.out.println("Pillar "+name+" dont have any master");
				flag = true;
			}else{
				if(!haveblocktoactivate){
					haveblocktoactivate = true;
					p.blockToActivate = new BlockPos(p.blockMaster.getX(), p.blockMaster.getY(), p.blockMaster.getZ());
				}
			}
			if(!flag && haveblocktoactivate){
				for(BlockPos co : ms){
					int nx = co.getX() + p.blockMaster.getX()*-1;
					int ny = co.getY() + p.blockMaster.getY()*-1;
					int nz = co.getZ() + p.blockMaster.getZ()*-1;
					p.mapStructure.put(nx+"_"+ny+"_"+nz, new BlockPos(nx, ny, nz));
				}
				for(Entry<String, String> entry : inte.entrySet()){
					int[] c = getXYZ(entry.getKey(), "_");
					int ix = c[0] + p.blockMaster.getX()*-1;
					int iy = c[1] + p.blockMaster.getY()*-1;
					int iz = c[2] + p.blockMaster.getZ()*-1;
					String[] t = entry.getValue().split("[,]");
					if(t!=null && t.length>=1){
						int[] ta = new int[t.length];
						for(int i = 0;i<t.length;i++)
							ta[i] = Integer.parseInt(t[i]);
						p.interfaces.put(ix+"_"+iy+"_"+iz,ta );
					}
				}
				p.blockToActivate.add(p.blockMaster.getX()*-1, p.blockMaster.getY()*-1, p.blockMaster.getZ()*-1);
				p.blockMaster.add(-p.blockMaster.getX(), -p.blockMaster.getY(), -p.blockMaster.getZ());
			}
			//dd
			if(!flag)
				nameToPillar.put(name, p);
		}
	}
	
	public int[] getXYZ(String str, String sep){
		String[] separated = str.split("["+sep+"]");
		return new int[]{Integer.parseInt(separated[0]),Integer.parseInt(separated[1]),Integer.parseInt(separated[2])};
	}
	
	public Pillar getPillarFromName(String name){
		return nameToPillar.containsKey(name)?nameToPillar.get(name):null;
	}
	
	public class Pillar{
		public final String name;//UID
		public int ID;
		public int energyCapacity = 0;
		public int fluidCapacity = 0;
		public float naturalRatio = 1;
		public Map<String,BlockPos> mapStructure = new HashMap<String, BlockPos>();
		public Map<String,int[]> interfaces = new HashMap<String, int[]>();
		public BlockPos blockToActivate;
		public BlockPos blockMaster;
		
		public Pillar(String name){
			this.name = name;
		}

		public boolean checkStructure(World w, BlockPos pos, Block originalb) {
			
			int size = mapStructure.size();
			for(Entry<String, BlockPos> entry : mapStructure.entrySet()){
				BlockPos npos = entry.getValue().add(pos.getX(), pos.getY(), pos.getZ());
		    	IBlockState state = w.getBlockState(npos);
				Block b = state.getBlock();
				TileEntity t = w.getTileEntity(npos);
				if(b==null || b.getClass()!=originalb.getClass()){
					return false;
				}
				if(t!=null){
					if( ( t instanceof TileMasterPillar && ((TileMasterPillar)t).hasMaster() ) ){
						return false;
					}
					if( t instanceof TileMasterPillar ==false && t instanceof TileRenderPillarModel==false){
						return false;
					}
				}
			}
			return true;
		}	
		
		public boolean reCheckStructure(IBlockAccess w, BlockPos pos, Block originalb) {
			for(Entry<String, BlockPos> entry : mapStructure.entrySet()){
				BlockPos npos = entry.getValue().add(pos.getX(), pos.getY(), pos.getZ());
		    	IBlockState state = w.getBlockState(npos);
				Block b = state.getBlock();
		    	if(b==Blocks.AIR)
		    		return false;
				TileEntity t = w.getTileEntity(npos);
				if(b==null || b.getClass()!=originalb.getClass()){
					return false;
				}
				if(t!=null){
					//if( ( t instanceof TileMasterPillar && ((TileMasterPillar)t).hasMaster() ) ){
					//	return false;
					//}
					if( t instanceof TileMasterPillar ==false && t instanceof TileRenderPillarModel==false && t instanceof TileBlockPillar==false){
						return false;
					}
				}
			}
			return true;
		}
		
		public void setupStructure(World w, EntityPlayer player, BlockPos pos) {

			for(Entry<String, BlockPos> entry : mapStructure.entrySet()){
				BlockPos npos = entry.getValue().add(pos.getX(), pos.getY(), pos.getZ());
		    	IBlockState state = w.getBlockState(npos);
				Block b = state.getBlock();
				TileEntity tb = w.getTileEntity(npos);
				
				if(tb!=null && tb instanceof TileMasterPillar && ((TileMasterPillar)tb).hasMaster())
					continue;
				
		    	if(tb!=null && tb instanceof TileRenderPillarModel){
					w.removeTileEntity(npos);
					w.setBlockState(pos, b.getDefaultState().withProperty(BlockMaterials.BLOCK_TYPE, EnumPillarBlockType.NO_PILLAR_NORMAL));
					state = w.getBlockState(npos);
					b = state.getBlock();
		    	}
		    	
		    	if(interfaces.containsKey(entry.getKey())){
		    		w.setBlockState(npos, b.getDefaultState().withProperty(BlockMaterials.BLOCK_TYPE, EnumPillarBlockType.PILLAR_INTERFACE));
		    		TileEntity te = (TileEntity)w.getTileEntity(npos);
		    		TileInterfacePillar tip;
		        	if(te!=null && te instanceof TileInterfacePillar){
		        		tip = (TileInterfacePillar)te;
		    	        tip.master = pos;
		    	        for(int k = 0 ; k<interfaces.get(entry.getKey()).length; k++){
			            	tip.extractSides.add(EnumFacing.getFront(interfaces.get(entry.getKey())[k]));
		 	        		tip.recieveSides.add(EnumFacing.getFront(interfaces.get(entry.getKey())[k]));
			            }
			            tip.init();
			            tip.markDirty();
		        	}
		    	}else{
		    		w.setBlockState(npos, b.getDefaultState().withProperty(BlockMaterials.BLOCK_TYPE, EnumPillarBlockType.PILLAR_NORMAL));
		    		TileEntity te = (TileEntity)w.getTileEntity(npos);
		    	   	TileBlockPillar tbp;
		        	if(te!=null && te instanceof TileBlockPillar){
		        		tbp = (TileBlockPillar)te;
			    		tbp.master = pos;
			    		tbp.init();
			    		tbp.markDirty();
		    		}
		    	}
		    	w.scheduleUpdate(npos, b, 0);
		    	//w.notifyBlockUpdate(pos, w.getBlockState(pos), w.getBlockState(pos), 3);
		    	//w.notifyNeighborsOfStateChange(npos, b);
			}
		}
	}
}