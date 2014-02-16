package com.jclc.busclient;

import java.util.ArrayList;

public class Model {

    public static ArrayList<Item> Items;

    public static void LoadModel(String image, ArrayList<String> tickets) {

        Items = new ArrayList<Item>();
        int i=0;
        while(i < tickets.size()){
            Items.add(new Item(i+1, image, tickets.get(i)));
            i++;
        }

    }

    public static Item GetbyId(int id){

        for(Item item : Items) {
            if (item.Id == id) {
                return item;
            }
        }
        return null;
    }

}