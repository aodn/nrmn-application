package au.org.aodn.nrmn.restapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import au.org.aodn.nrmn.restapi.model.db.Survey;
import au.org.aodn.nrmn.restapi.repository.projections.LetterCodeMapping;

@Repository
public interface LetterCodeRespository extends JpaRepository<Survey, Integer>, JpaSpecificationExecutor<Survey> {

    @Query(value = "WITH stage0 AS (SELECT COALESCE(o.superseded_by, o.observable_item_name) AS species_name, o.observable_item_id, SUM(b.measure_value) AS abundance from nrmn.observable_item_ref o "
            + "inner join nrmn.methods_species ms ON ms.observable_item_id = o.observable_item_id "
            + "inner join nrmn.observation b on o.observable_item_id = b.observable_item_id "
            + "inner join nrmn.survey_method m on b.survey_method_id = m.method_id "
            + "inner join nrmn.survey s on s.survey_id = m.survey_id "
            + "inner join nrmn.site_ref i on s.site_id = i.site_id "
            + "WHERE i.site_id in (:siteIds) "
            + "GROUP BY o.observable_item_id, species_name, common_name),"
            + "stage1 AS (SELECT *, nrmn.abbreviated_species_code(species_name, 3) AS code_len3 FROM stage0),"
            + "stage2 AS (SELECT *, ROW_NUMBER() OVER (partition by code_len3 order by abundance desc) AS code_len3_rank FROM stage1),"
            + "stage3 AS (SELECT *, CASE WHEN code_len3_rank = 1 THEN NULL ELSE nrmn.abbreviated_species_code(species_name, 4) END code_len4 FROM stage2),"
            + "stage4 AS (SELECT *, ROW_NUMBER() OVER (partition by code_len4 order by abundance desc) AS code_len4_rank FROM stage3),"
            + "stage5 AS (SELECT *, CASE WHEN (code_len4 is NULL or code_len4_rank = 1) THEN NULL ELSE nrmn.abbreviated_species_code(species_name, 5) END code_len5 FROM stage4),"
            + "stage6 AS (SELECT *, CAST(ROW_NUMBER() over (partition by code_len5 order by abundance desc) AS INT) AS code_len5_rank FROM stage5),"
            + "stage7 AS (SELECT *, CASE WHEN (code_len5 is NULL or code_len5_rank = 1) THEN NULL ELSE nrmn.abbreviated_species_code(species_name, 5 + code_len5_rank - 1) END AS code_len_max FROM stage6) "
            + "SELECT oir.observable_item_id as observableItemId, "
            + "LOWER(COALESCE(s7.code_len_max, s7.code_len5, s7.code_len4, s7.code_len3)) AS letterCode "
            + "FROM nrmn.observable_item_ref oir JOIN stage7 s7 on s7.observable_item_id = oir.observable_item_id "
            + "JOIN nrmn.obs_item_type_ref oitr ON oitr.obs_item_type_id = oir.obs_item_type_id "
            + "WHERE oitr.obs_item_type_id = ANY (ARRAY[1, 2]) ORDER BY letterCode", nativeQuery = true)
    List<LetterCodeMapping> findByCriteria(@Param("siteIds") List<Integer> siteIds);
}
