import csv
import requests
import time
from urllib.parse import quote


# Extracts all available taxonomic information from the WoRMS website
# for the current list of observable_item_id values (as of 20 Apr 2026).
# The script reads the observable item list prepared using CleanSpeciesList.py
# and outputs the results to worms_taxonomy.csv



BASE_URL = "https://www.marinespecies.org/rest"


#use DB extract cleaned using CleanSpecies List
INPUT_CSV = "CleanList/Obs_item_list_clean.csv"
OUTPUT_CSV = "worms_taxonomy.csv"

RANK_FIELDS = [
    "rank_kingdom",
    "rank_phylum",
    "rank_class",
    "rank_order",
    "rank_family",
    "rank_genus",
]

OUTPUT_FIELDS = [
    "observable_item_id",
    "observable_item_name",
    "aphia_id",
    "url",
    "scientificname",
    "authority",
    "status",
    "unacceptreason",
    "taxon_rank_id",
    "rank",
    "valid_aphia_id",
    "valid_name",
    "valid_authority",
    "parent_name_usage_id",
    *RANK_FIELDS,
    "citation",
    "lsid",
    "is_marine",
    "is_brackish",
    "is_freshwater",
    "is_terrestrial",
    "is_extinct",
    "match_type",
    "modified",
]


def get_accepted_record(name):
    url = f"{BASE_URL}/AphiaRecordsByName/{quote(name)}"
    params = {"like": "false", "marine_only": "true"}

    r = requests.get(url, params=params, timeout=30)
    r.raise_for_status()
    records = r.json()

    if not records:
        return None

    for rec in records:
        if rec.get("status") == "accepted":
            rec["match_type"] = "accepted"
            return rec

    records[0]["match_type"] = "unaccepted"
    return records[0]


def get_classification(aphia_id):
    url = f"{BASE_URL}/AphiaClassificationByAphiaID/{aphia_id}"
    r = requests.get(url, timeout=30)
    r.raise_for_status()
    return r.json()


def extract_ranks(node, ranks):
    if not isinstance(node, dict):
        return  # skip invalid nodes

    rank = node.get("rank", "").lower()
    name = node.get("scientificname")

    key = f"rank_{rank}"
    if key in ranks:
        ranks[key] = name

    children = node.get("child")

    if isinstance(children, dict):
        extract_ranks(children, ranks)

    elif isinstance(children, list):
        for child in children:
            extract_ranks(child, ranks)


def main():
    with open(INPUT_CSV, newline="", encoding="utf-8") as infile, \
         open(OUTPUT_CSV, "w", newline="", encoding="utf-8") as outfile:

        reader = csv.DictReader(infile)
        writer = csv.DictWriter(outfile, fieldnames=OUTPUT_FIELDS)
        writer.writeheader()

        for row in reader:
            obs_item_id = row.get("observable_item_id", "")
            name = row.get("observable_item_name", "")

            out = {field: "" for field in OUTPUT_FIELDS}

            # Add your two input columns
            out["observable_item_id"] = obs_item_id
            out["observable_item_name"] = name

            try:
                record = get_accepted_record(name)
                if not record:
                    raise ValueError("No WoRMS record found")

                aphia_id = record.get("valid_AphiaID") or record["AphiaID"]

                ranks = {k: "" for k in RANK_FIELDS}
                classification = get_classification(aphia_id)
                if not isinstance(classification, dict):
                    raise ValueError(f"Unexpected classification format: {classification}")

                extract_ranks(classification, ranks)

                out.update({
                    "aphia_id": record.get("AphiaID"),
                    "url": record.get("url"),
                    "scientificname": record.get("scientificname"),
                    "authority": record.get("authority"),
                    "status": record.get("status"),
                    "unacceptreason": record.get("unacceptreason"),
                    "taxon_rank_id": record.get("taxonRankID"),
                    "rank": record.get("rank"),
                    "valid_aphia_id": record.get("valid_AphiaID"),
                    "valid_name": record.get("valid_name"),
                    "valid_authority": record.get("valid_authority"),
                    "parent_name_usage_id": record.get("parentNameUsageID"),
                    "citation": record.get("citation"),
                    "lsid": record.get("lsid"),
                    "is_marine": record.get("isMarine"),
                    "is_brackish": record.get("isBrackish"),
                    "is_freshwater": record.get("isFreshwater"),
                    "is_terrestrial": record.get("isTerrestrial"),
                    "is_extinct": record.get("isExtinct"),
                    "match_type": record.get("match_type"),
                    "modified": record.get("modified"),
                })

                out.update(ranks)

            except Exception as e:
                out["match_type"] = f"ERROR: {e}"

            writer.writerow(out)

            time.sleep(0.2)


if __name__ == "__main__":
    main()
