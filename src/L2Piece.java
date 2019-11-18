public class L2Piece extends Cache
{

    public L2Piece(int size, int blocksize, int assoc, int core_id)
    {
        super(size, blocksize, assoc, core_id);
    }

    public void handleEvictedBlock(CacheBlock cacheblock)
	{
		L2Arbiter.removeL2(cacheblock, core_id);
    }
    
    public void add(long address, int rw)
    {
        cache_access(address, rw);
    }
}
