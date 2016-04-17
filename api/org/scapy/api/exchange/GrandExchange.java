package org.scapy.api.exchange;

import org.json.JSONException;
import org.json.JSONObject;
import org.scapy.utils.WebUtilities;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A facility for querying the Grand Exchange catalog. This class does not cache
 * any returned values.
 *
 * @author Martin Tuskevicius
 */
public final class GrandExchange {

    private static final String GRAND_EXCHANGE_FORMAT = "http://services.runescape.com/m=itemdb_oldschool/api/catalogue/detail.json?item=%d";
    private static final int GOLD_ID = 995;

    /**
     * Prevents external initialization.
     */
    private GrandExchange() {

    }

    /**
     * Queries the Grand Exchange catalog for an item listing. The returned
     * object is immutable.
     *
     * @param id the item ID.
     * @return the item listing.
     * @throws IOException            if an I/O error occurs.
     * @throws GrandExchangeException if no listing for the provided ID exists,
     *                                or if the response is malformed.
     */
    public static GrandExchangeItem lookup(int id) throws IOException {
        try {
            return new GrandExchangeItem(id, new JSONObject(WebUtilities.downloadPageSource(String.format(GRAND_EXCHANGE_FORMAT, id))));
        } catch (FileNotFoundException e) {
            throw new GrandExchangeException("No item listing found for " + id + ".");
        } catch (JSONException e) {
            throw new GrandExchangeException("Malformed response for " + id + ".", e);
        }
    }

    /**
     * A bulk operation for querying the Grand Exchange catalog for item
     * listings. Each value in the returned array corresponds to an element in
     * <code>ids</code>. If an item could not be looked up, a <code>null</code>
     * element takes its place in the returned array. The elements in the
     * returned array are immutable.
     *
     * @param ids the array of item IDs.
     * @return the item listings.
     * @throws IOException if an I/O error occurs.
     */
    public static GrandExchangeItem[] lookup(int ... ids) throws IOException {
        GrandExchangeItem[] items = new GrandExchangeItem[ids.length];
        for (int i = 0; i < ids.length; i++) {
            try {
                items[i] = lookup(ids[i]);
            } catch (GrandExchangeException ignored) {}
        }
        return items;
    }

    /**
     * Downloads an item's icon. The item must be in the Grand Exchange catalog.
     *
     * @param id the item ID.
     * @return the icon.
     * @throws IOException            if an I/O error occurs.
     * @throws GrandExchangeException if no listing for the provided ID exists,
     *                                or if the response is malformed.
     */
    public static BufferedImage downloadIcon(int id) throws IOException {
        return lookup(id).downloadIcon();
    }

    /**
     * Queries the Grand Exchange catalog for an item's current price.
     *
     * @param id the item ID.
     * @return the item price.
     * @throws IOException            if an I/O error occurs.
     * @throws GrandExchangeException if no listing for the provided ID exists,
     *                                or if the response is malformed.
     */
    public static int price(int id) throws IOException {
        return (id == GOLD_ID) ? 1 : lookup(id).currentPrice;
    }
}