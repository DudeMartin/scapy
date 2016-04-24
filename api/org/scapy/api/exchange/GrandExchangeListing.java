package org.scapy.api.exchange;

import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * An immutable representation of a Grand Exchange item listing.
 *
 * @author Martin Tuskevicius
 */
public final class GrandExchangeListing {

    /**
     * The item ID.
     */
    public final int id;

    /**
     * The remote address of this item's icon.
     */
    public final String iconAddress;

    /**
     * The remote address of this item's large icon.
     */
    public final String largeIconAddress;

    /**
     * The item type.
     */
    public final String type;

    /**
     * The remote address of this item type's icon.
     */
    public final String typeIconAddress;

    /**
     * The item name.
     */
    public final String name;

    /**
     * The item description.
     */
    public final String description;

    /**
     * The item's current price.
     */
    public final int currentPrice;

    /**
     * The amount by which the item price has changed today.
     */
    public final int todayChange;

    /**
     * If this item is members-only.
     */
    public final boolean members;

    /**
     * The percentage by which the item price has changed over the last 30 days.
     */
    public final double day30Change;

    /**
     * The percentage by which the item price has changed over the last 90 days.
     */
    public final double day90Change;

    /**
     * The percentage by which the item price has changed over the last 180 days.
     */
    public final double day180Change;

    GrandExchangeListing(int id, JSONObject source) {
        JSONObject item = source.getJSONObject("item");
        this.id = id;
        this.iconAddress = item.getString("icon");
        this.largeIconAddress = item.getString("icon_large");
        this.type = item.getString("type");
        this.typeIconAddress = item.getString("typeIcon");
        this.name = item.getString("name");
        this.description = item.getString("description");
        this.currentPrice = parseAmountTrend(item.getJSONObject("current"));
        this.todayChange = parseAmountTrend(item.getJSONObject("today"));
        this.members = Boolean.parseBoolean(item.getString("members"));
        this.day30Change = parseChangeTrend(item.getJSONObject("day30"));
        this.day90Change = parseChangeTrend(item.getJSONObject("day90"));
        this.day180Change = parseChangeTrend(item.getJSONObject("day180"));
    }

    /**
     * Downloads this item's icon. The returned image is not internally cached.
     *
     * @return the icon.
     * @throws IOException if an I/O error occurs.
     */
    public BufferedImage downloadIcon() throws IOException {
        return ImageIO.read(new URL(iconAddress));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof GrandExchangeListing) {
            GrandExchangeListing item = (GrandExchangeListing) obj;
            return item.id == id
                    && item.iconAddress.equals(iconAddress)
                    && item.largeIconAddress.equals(largeIconAddress)
                    && item.type.equals(type)
                    && item.typeIconAddress.equals(typeIconAddress)
                    && item.name.equals(name)
                    && item.description.equals(description)
                    && item.currentPrice == currentPrice
                    && item.todayChange == todayChange
                    && item.members == members
                    && item.day30Change == day30Change
                    && item.day90Change == day90Change
                    && item.day180Change == day180Change;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
                iconAddress,
                largeIconAddress,
                type,
                typeIconAddress,
                name,
                description,
                currentPrice,
                todayChange,
                members,
                day30Change,
                day90Change,
                day180Change);
    }

    private static int parseAmountTrend(JSONObject source) {
        Object price = source.get("price");
        if (price instanceof Integer) {
            return (Integer) price;
        } else if (price instanceof String) {
            String priceString = (String) price;
            StringBuilder builder = new StringBuilder(11);
            for (int i = 0; i < priceString.length(); i++) {
                char c = priceString.charAt(i);
                if (Character.isDigit(c) || c == '-') {
                    builder.append(c);
                } else if (Character.isLetter(c)) {
                    switch (c) {
                        case 'k':
                            builder.append("00");
                            break;
                        case 'm':
                            builder.append("00000");
                            break;
                        case 'b':
                            builder.append("00000000");
                            break;
                    }
                }
            }
            return Integer.parseInt(builder.toString());
        }
        throw new JSONException("Unexpected price trend amount type.");
    }

    private static double parseChangeTrend(JSONObject source) {
        String changeString = source.getString("change");
        StringBuilder builder = new StringBuilder(8);
        for (int i = 0; i < changeString.length(); i++) {
            char c = changeString.charAt(i);
            if (Character.isDigit(c) || c == '.' || c == '-') {
                builder.append(c);
            }
        }
        return Double.parseDouble(builder.toString());
    }
}