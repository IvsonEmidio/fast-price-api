package com.reconnect.fast_price_api.dto;

import java.util.List;

public class AliexpressResponse {
    private Result result;

    public Result getResult() {
        return result;
    }

    public static class Result {
        private Item item;

        public Item getItem() {
            return item;
        }
    }

    public static class Item {
        private Sku sku;

        public Sku getSku() {
            return sku;
        }
    }

    public static class Sku {
        private List<Base> base;

        public List<Base> getBase() {
            return base;
        }
    }

    public static class Base {
        private String skuId;
        private String skuAttr;

        public String getSkuId() {
            return skuId;
        }

        public String getSkuAttr() {
            return skuAttr;
        }
    }
} 