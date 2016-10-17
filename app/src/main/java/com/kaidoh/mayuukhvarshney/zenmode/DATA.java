package com.kaidoh.mayuukhvarshney.zenmode;

/**
 * Created by mayuukhvarshney on 16/10/16.
 */
public class DATA {

    private String ZenHours,MoveHours,Date;
    public void setZenHours(String hrs)
    {
      this.ZenHours = hrs;
    }
    public void setMoveHours(String hrs)
    {
        this.MoveHours = hrs;
    }
    public void setDate(String hrs)
    {
        this.Date = hrs;
    }

    public String getZenHours()
    {
        return this.ZenHours;
    }
    public String getMoveHours()
    {
        return this.MoveHours;
    }
    public String getDate()
    {
        return this.Date;
    }
}
